package com.example.calculator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calculator.ui.theme.CalcTheme
import com.example.calculator.viewmodel.CalculatorViewModel

// ─── Basic Keypad ─────────────────────────────────────────────────────────────

private val BASIC_KEYS = listOf(
    listOf("C",  "CE",  "%",  "÷"),
    listOf("7",  "8",   "9",  "×"),
    listOf("4",  "5",   "6",  "-"),
    listOf("1",  "2",   "3",  "+"),
    listOf("±",  "0",   ".",  "=")
)

@Composable
fun BasicKeypad(vm: CalculatorViewModel, theme: CalcTheme) {
    KeypadGrid(
        rows = BASIC_KEYS,
        vm = vm,
        theme = theme
    )
}

// ─── Scientific Keypad ────────────────────────────────────────────────────────

private val SCI_ROWS_TOP = listOf(
    listOf("sin(",  "cos(",  "tan(",  "π",    "e"),
    listOf("log(",  "ln(",   "√(",    "^",    "!"),
    listOf("(",     ")",     "asin(", "acos(","atan("),
)
private val SCI_ROWS_BOTTOM = listOf(
    listOf("C",    "⌫",    "%",    "÷",    "×"),
    listOf("7",    "8",    "9",    "-",    "+"),
    listOf("4",    "5",    "6",    ".",    "="),
    listOf("1",    "2",    "3",    "0",    "±"),
)
private val SCI_KEYS_TOP_FLAT = SCI_ROWS_TOP.flatten().toSet()

@Composable
fun ScientificKeypad(vm: CalculatorViewModel, theme: CalcTheme) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        // Scientific function rows
        SCI_ROWS_TOP.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                row.forEach { key ->
                    CalcButton(
                        key = key,
                        theme = theme,
                        modifier = Modifier.weight(1f).height(42.dp),
                        btnType = BtnType.SCIENTIFIC,
                        onClick = { vm.onKey(key) }
                    )
                }
            }
        }
        // Normal rows
        SCI_ROWS_BOTTOM.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                row.forEach { key ->
                    val type = classifyKey(key)
                    CalcButton(
                        key = key,
                        theme = theme,
                        modifier = Modifier.weight(1f).height(50.dp),
                        btnType = type,
                        onClick = { vm.onKey(key) },
                        onLongClick = if (key == "⌫" || key == "C") {{ vm.onKey("C") }} else null
                    )
                }
            }
        }
    }
}

// ─── Programmer Keypad ────────────────────────────────────────────────────────

private val PROG_ROWS = listOf(
    listOf("A",    "B",    "C",    "D",    "E",    "F"),
    listOf("C",    "⌫",    "(",    ")",    "÷",    "×"),
    listOf("7",    "8",    "9",    "-",    "+",    "="),
    listOf("4",    "5",    "6",    "0b",   "0x",   "0o"),
    listOf("1",    "2",    "3",    "0",    ".",    "±"),
)
private val PROG_SPECIAL = setOf("A", "B", "C", "D", "E", "F", "0b", "0x", "0o")

@Composable
fun ProgrammerKeypad(vm: CalculatorViewModel, theme: CalcTheme) {
    KeypadGrid(
        rows = PROG_ROWS,
        vm = vm,
        theme = theme,
        classifyOverride = { key ->
            when {
                key in PROG_SPECIAL -> BtnType.SCIENTIFIC
                else -> null
            }
        }
    )
}

// ─── Generic Grid ─────────────────────────────────────────────────────────────

@Composable
fun KeypadGrid(
    rows: List<List<String>>,
    vm: CalculatorViewModel,
    theme: CalcTheme,
    classifyOverride: ((String) -> BtnType?)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                row.forEach { key ->
                    val type = classifyOverride?.invoke(key) ?: classifyKey(key)
                    CalcButton(
                        key = key,
                        theme = theme,
                        modifier = Modifier.weight(1f).aspectRatio(1.15f),
                        btnType = type,
                        onClick = { vm.onKey(key) },
                        onLongClick = if (key == "⌫" || key == "C") {{ vm.onKey("C") }} else null
                    )
                }
            }
        }
    }
}
