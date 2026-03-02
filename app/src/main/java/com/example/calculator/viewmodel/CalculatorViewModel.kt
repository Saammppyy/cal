package com.example.calculator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.calculator.engine.CalculatorEngine
import com.example.calculator.ui.theme.CalcThemeId

enum class CalcMode { BASIC, SCIENTIFIC, PROGRAMMER }

data class HistoryEntry(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

class CalculatorViewModel : ViewModel() {

    // ─── Theme ────────────────────────────────────────────────────────────────
    var themeId by mutableStateOf(CalcThemeId.DARK_LUXURY)
        private set

    fun setTheme(id: CalcThemeId) { themeId = id }

    // ─── Mode ─────────────────────────────────────────────────────────────────
    var mode by mutableStateOf(CalcMode.BASIC)
        private set

    fun updateMode(m: CalcMode) { mode = m }

    // ─── Calc State ───────────────────────────────────────────────────────────
    var expression by mutableStateOf("")
        private set

    var liveResult by mutableStateOf("")
        private set

    var displayResult by mutableStateOf("0")
        private set

    var isError by mutableStateOf(false)
        private set

    var justEvaluated by mutableStateOf(false)
        private set

    var angleMode by mutableStateOf("DEG")
        private set

    // ─── Suggestions ──────────────────────────────────────────────────────────
    val suggestions = mutableStateListOf<Pair<String, String>>()

    // ─── History ──────────────────────────────────────────────────────────────
    val history = mutableStateListOf<HistoryEntry>()

    // ─── Programmer ───────────────────────────────────────────────────────────
    var programmerValue by mutableStateOf<Double?>(null)
        private set

    // ─── Key Handling ─────────────────────────────────────────────────────────
    fun onKey(key: String) {
        when (key) {
            "="    -> evaluate()
            "C"    -> clearAll()
            "CE"   -> clearEntry()
            "⌫"    -> backspace()
            "±"    -> toggleSign()
            else   -> append(key)
        }
    }

    private fun append(key: String) {
        if (justEvaluated) {
            expression = if (key in listOf("+", "-", "×", "÷", "^")) displayResult + key
            else key
            justEvaluated = false
        } else {
            expression += key
        }
        updateLive()
    }

    private fun evaluate() {
        val expr = if (expression.isNotBlank()) expression
                   else if (justEvaluated) return
                   else displayResult
        val r = CalculatorEngine.finalEval(expr)
        isError = r.isError
        if (!r.isError && r.value != null) {
            history.add(0, HistoryEntry(expr, r.formatted))
            if (history.size > 100) history.removeAt(history.size - 1)
            displayResult = r.formatted
            programmerValue = r.value
            suggestions.clear()
            suggestions.addAll(CalculatorEngine.getQuickSuggestions(r.value))
        } else {
            displayResult = r.formatted
            programmerValue = null
        }
        expression = ""
        liveResult = ""
        justEvaluated = true
    }

    private fun updateLive() {
        val r = CalculatorEngine.liveEval(expression)
        liveResult = if (r.formatted.isNotEmpty() && r.formatted != expression) r.formatted else ""
        suggestions.clear()
    }

    private fun clearAll() {
        expression = ""; liveResult = ""; displayResult = "0"
        isError = false; justEvaluated = false
        programmerValue = null; suggestions.clear()
    }

    private fun clearEntry() {
        expression = ""; liveResult = ""; isError = false; suggestions.clear()
    }

    fun backspace() {
        if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
            updateLive()
        } else if (justEvaluated) {
            if (displayResult.length > 1) {
                expression = displayResult.dropLast(1)
                displayResult = "0"
                justEvaluated = false
                updateLive()
            } else {
                displayResult = "0"; justEvaluated = false
            }
        }
    }

    private fun toggleSign() {
        if (justEvaluated) {
            val num = displayResult.toDoubleOrNull() ?: return
            displayResult = CalculatorEngine.formatResult(-num)
        } else if (expression.isNotEmpty()) {
            expression = if (expression.startsWith("-")) expression.substring(1) else "-$expression"
            updateLive()
        }
    }

    fun toggleAngleMode() { angleMode = if (angleMode == "DEG") "RAD" else "DEG" }

    // ─── History ops ──────────────────────────────────────────────────────────
    fun useHistoryEntry(e: HistoryEntry) {
        expression = ""; displayResult = e.result
        justEvaluated = true; liveResult = ""; suggestions.clear()
        programmerValue = e.result.toDoubleOrNull()
    }

    fun clearHistory() { history.clear() }

    // ─── Programmer helpers ───────────────────────────────────────────────────
    fun getHex()    = programmerValue?.let { CalculatorEngine.toHex(it) } ?: "—"
    fun getBinary() = programmerValue?.let { CalculatorEngine.toBinary(it) } ?: "—"
    fun getOctal()  = programmerValue?.let { CalculatorEngine.toOctal(it) } ?: "—"
}
