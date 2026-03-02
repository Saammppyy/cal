package com.example.calculator.engine

import kotlin.math.*

/**
 * LuxCalc Expression Engine
 * Supports: basic ops, scientific functions, programmer mode (hex/bin/oct),
 * live preview, history, and smart suggestions.
 */
object CalculatorEngine {

    data class EvalResult(
        val value: Double?,
        val formatted: String,
        val isError: Boolean = false,
        val errorMsg: String = ""
    )

    // ─── Live Preview ────────────────────────────────────────────────────────

    fun liveEval(expression: String): EvalResult {
        if (expression.isBlank()) return EvalResult(null, "")
        return try {
            val sanitized = sanitize(expression)
            val result = evaluate(sanitized)
            EvalResult(result, formatResult(result))
        } catch (e: Exception) {
            EvalResult(null, "")
        }
    }

    fun finalEval(expression: String): EvalResult {
        if (expression.isBlank()) return EvalResult(null, "0")
        return try {
            val sanitized = sanitize(expression)
            val result = evaluate(sanitized)
            if (result.isInfinite()) return EvalResult(null, "∞", true, "Division by zero")
            if (result.isNaN()) return EvalResult(null, "Error", true, "Undefined")
            EvalResult(result, formatResult(result))
        } catch (e: Exception) {
            EvalResult(null, "Syntax Error", true, e.message ?: "Invalid expression")
        }
    }

    // ─── Smart Suggestions ───────────────────────────────────────────────────

    fun getQuickSuggestions(result: Double): List<Pair<String, String>> {
        val suggestions = mutableListOf<Pair<String, String>>()

        // Percentage
        suggestions.add(Pair("% →", formatResult(result / 100)))

        // Square root
        if (result > 0) suggestions.add(Pair("√ →", formatResult(sqrt(result))))

        // Inverse
        if (result != 0.0) suggestions.add(Pair("1/x →", formatResult(1.0 / result)))

        // Scientific notation if large
        if (abs(result) >= 1_000_000) {
            suggestions.add(Pair("sci →", String.format("%.3e", result)))
        }

        return suggestions.take(3)
    }

    // ─── Programmer Mode ─────────────────────────────────────────────────────

    fun toHex(value: Double): String {
        val long = value.toLong()
        return "0x${long.toString(16).uppercase()}"
    }

    fun toBinary(value: Double): String {
        val long = value.toLong()
        return "0b${long.toString(2)}"
    }

    fun toOctal(value: Double): String {
        val long = value.toLong()
        return "0o${long.toString(8)}"
    }

    // ─── Internals ───────────────────────────────────────────────────────────

    private fun sanitize(expr: String): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", Math.PI.toString())
            .replace(Regex("(?<![a-zA-Z_])e(?![a-zA-Z_(])"), Math.E.toString())
            .replace("%", "/100")
            .replace("√(", "sqrt(")
            .replace("sin(", "sin_deg(")
            .replace("cos(", "cos_deg(")
            .replace("tan(", "tan_deg(")
    }

    private fun evaluate(expr: String): Double {
        // Recursive descent parser
        val tokens = tokenize(expr)
        val parser = Parser(tokens)
        return parser.parseExpression()
    }

    fun formatResult(value: Double): String {
        return if (value == kotlin.math.floor(value) && !value.isInfinite() && abs(value) < 1e15) {
            value.toLong().toString()
        } else {
            // Up to 10 significant digits, strip trailing zeros
            val formatted = "%.10g".format(value)
            formatted.trimEnd('0').trimEnd('.')
        }
    }

    // ─── Tokenizer ───────────────────────────────────────────────────────────

    private sealed class Token {
        data class Number(val value: Double) : Token()
        data class Op(val char: Char) : Token()
        data class Func(val name: String) : Token()
        object LParen : Token()
        object RParen : Token()
    }

    private fun tokenize(expr: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        val s = expr.trim()
        while (i < s.length) {
            when {
                s[i].isWhitespace() -> i++
                s[i].isDigit() || s[i] == '.' -> {
                    val start = i
                    while (i < s.length && (s[i].isDigit() || s[i] == '.' || s[i] == 'E' || (s[i] == '-' && i > 0 && s[i-1] == 'E'))) i++
                    tokens.add(Token.Number(s.substring(start, i).toDouble()))
                }
                s[i].isLetter() -> {
                    val start = i
                    while (i < s.length && (s[i].isLetter() || s[i] == '_')) i++
                    val name = s.substring(start, i)
                    tokens.add(Token.Func(name))
                }
                s[i] == '(' -> { tokens.add(Token.LParen); i++ }
                s[i] == ')' -> { tokens.add(Token.RParen); i++ }
                s[i] == '!' -> { tokens.add(Token.Op('!')); i++ }
                s[i] in "+-*/^" -> { tokens.add(Token.Op(s[i])); i++ }
                else -> i++
            }
        }
        return tokens
    }

    // ─── Recursive Descent Parser ────────────────────────────────────────────

    private class Parser(private val tokens: List<Token>) {
        private var pos = 0

        fun parseExpression(): Double = parseAddSub()

        private fun parseAddSub(): Double {
            var left = parseMulDiv()
            while (pos < tokens.size) {
                val tok = tokens[pos]
                if (tok is Token.Op && (tok.char == '+' || tok.char == '-')) {
                    pos++
                    val right = parseMulDiv()
                    left = if (tok.char == '+') left + right else left - right
                } else break
            }
            return left
        }

        private fun parseMulDiv(): Double {
            var left = parsePower()
            while (pos < tokens.size) {
                val tok = tokens[pos]
                if (tok is Token.Op && (tok.char == '*' || tok.char == '/')) {
                    pos++
                    val right = parsePower()
                    left = if (tok.char == '*') left * right else left / right
                } else break
            }
            return left
        }

        private fun parsePower(): Double {
            var base = parseUnary()
            if (pos < tokens.size && tokens[pos] is Token.Op && (tokens[pos] as Token.Op).char == '^') {
                pos++
                val exp = parsePower()
                base = base.pow(exp)
            }
            return base
        }

        private fun parseUnary(): Double {
            if (pos < tokens.size && tokens[pos] is Token.Op) {
                val op = (tokens[pos] as Token.Op).char
                if (op == '-') { pos++; return -parseFactorial() }
                if (op == '+') { pos++; return parseFactorial() }
            }
            return parseFactorial()
        }

        private fun parseFactorial(): Double {
            var value = parsePrimary()
            while (pos < tokens.size && tokens[pos] is Token.Op && (tokens[pos] as Token.Op).char == '!') {
                pos++
                value = factorial(value.toInt()).toDouble()
            }
            return value
        }

        private fun parsePrimary(): Double {
            val tok = tokens.getOrNull(pos) ?: throw Exception("Unexpected end")

            return when (tok) {
                is Token.Number -> { pos++; tok.value }
                is Token.LParen -> {
                    pos++
                    val v = parseExpression()
                    if (pos < tokens.size && tokens[pos] is Token.RParen) pos++
                    v
                }
                is Token.Func -> {
                    pos++
                    when (tok.name) {
                        "sqrt" -> { val arg = parseParenArg(); sqrt(arg) }
                        "sin_deg" -> { val arg = parseParenArg(); sin(Math.toRadians(arg)) }
                        "cos_deg" -> { val arg = parseParenArg(); cos(Math.toRadians(arg)) }
                        "tan_deg" -> { val arg = parseParenArg(); tan(Math.toRadians(arg)) }
                        "asin" -> { val arg = parseParenArg(); Math.toDegrees(asin(arg)) }
                        "acos" -> { val arg = parseParenArg(); Math.toDegrees(acos(arg)) }
                        "atan" -> { val arg = parseParenArg(); Math.toDegrees(atan(arg)) }
                        "log" -> { val arg = parseParenArg(); log10(arg) }
                        "ln" -> { val arg = parseParenArg(); ln(arg) }
                        "abs" -> { val arg = parseParenArg(); abs(arg) }
                        "ceil" -> { val arg = parseParenArg(); ceil(arg) }
                        "floor" -> { val arg = parseParenArg(); floor(arg) }
                        else -> throw Exception("Unknown function: ${tok.name}")
                    }
                }
                else -> throw Exception("Unexpected token: $tok")
            }
        }

        private fun parseParenArg(): Double {
            if (pos < tokens.size && tokens[pos] is Token.LParen) {
                pos++
                val v = parseExpression()
                if (pos < tokens.size && tokens[pos] is Token.RParen) pos++
                return v
            }
            return parsePrimary()
        }

        private fun factorial(n: Int): Long {
            if (n < 0) throw Exception("Negative factorial")
            if (n > 20) throw Exception("Factorial too large")
            var result = 1L
            for (i in 2..n) result *= i
            return result
        }
    }
}
