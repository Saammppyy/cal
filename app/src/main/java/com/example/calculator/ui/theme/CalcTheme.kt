package com.example.calculator.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Theme Identity ──────────────────────────────────────────────────────────

enum class CalcThemeId {
    DARK_LUXURY,    // Deep navy + gold glass morphism
    BRUTALIST,      // Pure black/white, massive typography, raw edges
    MINIMAL,        // Off-white, hairline borders, extreme whitespace
    PLAYFUL         // Saturated candy colors, rounded, joyful
}

// ─── Theme Data Class ────────────────────────────────────────────────────────

data class CalcTheme(
    val id: CalcThemeId,
    val displayName: String,
    val emoji: String,

    // Backgrounds
    val background: Color,
    val displayBg: Color,
    val surfaceCard: Color,

    // Borders
    val borderColor: Color,
    val borderWidth: Dp,

    // Button colors
    val btnNumber: Color,
    val btnOperator: Color,
    val btnEquals: Color,
    val btnSpecial: Color,
    val btnScientific: Color,

    // Button shape
    val btnCornerRadius: Dp,
    val btnBorderColor: Color,
    val btnBorderWidth: Dp,

    // Text
    val textDisplay: Color,
    val textExpression: Color,
    val textBtnNumber: Color,
    val textBtnOperator: Color,
    val textBtnEquals: Color,
    val textBtnSpecial: Color,
    val textBtnScientific: Color,
    val textAccent: Color,
    val textDim: Color,

    // Display number size feel
    val displayFontWeight: FontWeight,

    // Glow / shadow effect
    val hasGlow: Boolean,
    val glowColor: Color,

    // History card
    val historyCardBg: Color,
    val historyAccent: Color,

    // Mode tab colors
    val tabSelected: Color,
    val tabUnselected: Color,
    val tabTextSelected: Color,
    val tabTextUnselected: Color,
    val tabBorder: Color,

    // Live preview dot
    val liveIndicatorColor: Color,

    // Suggestion chip
    val chipBg: Color,
    val chipBorder: Color,
    val chipText: Color,
)

// ─── Theme Definitions ────────────────────────────────────────────────────────

object CalcThemes {

    val DARK_LUXURY = CalcTheme(
        id = CalcThemeId.DARK_LUXURY,
        displayName = "Dark Luxury",
        emoji = "✦",

        background       = Color(0xFF060610),
        displayBg        = Color(0xFF080820),
        surfaceCard      = Color(0xFF0D0D1F),

        borderColor      = Color(0x33D4A853),
        borderWidth      = 0.5.dp,

        btnNumber        = Color(0xFF141428),
        btnOperator      = Color(0xFF1E1A38),
        btnEquals        = Color(0xFFD4A853),
        btnSpecial       = Color(0xFF0E1828),
        btnScientific    = Color(0xFF10101E),

        btnCornerRadius  = 18.dp,
        btnBorderColor   = Color(0x22D4A853),
        btnBorderWidth   = 0.5.dp,

        textDisplay      = Color(0xFFFFFFFF),
        textExpression   = Color(0xFF888899),
        textBtnNumber    = Color(0xFFEEEEFF),
        textBtnOperator  = Color(0xFFD4A853),
        textBtnEquals    = Color(0xFF060610),
        textBtnSpecial   = Color(0xFF6AABFF),
        textBtnScientific= Color(0xFFAA88FF),
        textAccent       = Color(0xFFD4A853),
        textDim          = Color(0xFF444460),

        displayFontWeight = FontWeight.Light,

        hasGlow          = true,
        glowColor        = Color(0x30D4A853),

        historyCardBg    = Color(0xFF0D0D22),
        historyAccent    = Color(0xFFD4A853),

        tabSelected      = Color(0x20D4A853),
        tabUnselected    = Color.Transparent,
        tabTextSelected  = Color(0xFFD4A853),
        tabTextUnselected= Color(0xFF444460),
        tabBorder        = Color(0xFFD4A853),

        liveIndicatorColor = Color(0xFFD4A853),

        chipBg     = Color(0x15D4A853),
        chipBorder = Color(0x40D4A853),
        chipText   = Color(0xFFD4A853),
    )

    val BRUTALIST = CalcTheme(
        id = CalcThemeId.BRUTALIST,
        displayName = "Brutalist",
        emoji = "■",

        background       = Color(0xFF0A0A0A),
        displayBg        = Color(0xFF000000),
        surfaceCard      = Color(0xFF111111),

        borderColor      = Color(0xFFFFFFFF),
        borderWidth      = 2.dp,

        btnNumber        = Color(0xFF1A1A1A),
        btnOperator      = Color(0xFF0A0A0A),
        btnEquals        = Color(0xFFFFFFFF),
        btnSpecial       = Color(0xFF0A0A0A),
        btnScientific    = Color(0xFF050505),

        btnCornerRadius  = 0.dp,
        btnBorderColor   = Color(0xFFFFFFFF),
        btnBorderWidth   = 1.5.dp,

        textDisplay      = Color(0xFFFFFFFF),
        textExpression   = Color(0xFF666666),
        textBtnNumber    = Color(0xFFFFFFFF),
        textBtnOperator  = Color(0xFFFF3333),
        textBtnEquals    = Color(0xFF000000),
        textBtnSpecial   = Color(0xFFFFFF00),
        textBtnScientific= Color(0xFF999999),
        textAccent       = Color(0xFFFF3333),
        textDim          = Color(0xFF333333),

        displayFontWeight = FontWeight.Black,

        hasGlow          = false,
        glowColor        = Color.Transparent,

        historyCardBg    = Color(0xFF111111),
        historyAccent    = Color(0xFFFF3333),

        tabSelected      = Color(0xFFFFFFFF),
        tabUnselected    = Color.Transparent,
        tabTextSelected  = Color(0xFF000000),
        tabTextUnselected= Color(0xFF444444),
        tabBorder        = Color(0xFFFFFFFF),

        liveIndicatorColor = Color(0xFFFF3333),

        chipBg     = Color(0xFF1A1A1A),
        chipBorder = Color(0xFFFF3333),
        chipText   = Color(0xFFFF3333),
    )

    val MINIMAL = CalcTheme(
        id = CalcThemeId.MINIMAL,
        displayName = "Minimal",
        emoji = "○",

        background       = Color(0xFFF5F5F0),
        displayBg        = Color(0xFFFFFFFF),
        surfaceCard      = Color(0xFFFFFFFF),

        borderColor      = Color(0xFFDDDDD8),
        borderWidth      = 1.dp,

        btnNumber        = Color(0xFFFFFFFF),
        btnOperator      = Color(0xFFF0F0EC),
        btnEquals        = Color(0xFF1A1A1A),
        btnSpecial       = Color(0xFFF0F0EC),
        btnScientific    = Color(0xFFF8F8F5),

        btnCornerRadius  = 12.dp,
        btnBorderColor   = Color(0xFFE5E5E0),
        btnBorderWidth   = 1.dp,

        textDisplay      = Color(0xFF1A1A1A),
        textExpression   = Color(0xFF999990),
        textBtnNumber    = Color(0xFF1A1A1A),
        textBtnOperator  = Color(0xFF444440),
        textBtnEquals    = Color(0xFFFFFFFF),
        textBtnSpecial   = Color(0xFF777770),
        textBtnScientific= Color(0xFF888880),
        textAccent       = Color(0xFF1A1A1A),
        textDim          = Color(0xFFBBBBB0),

        displayFontWeight = FontWeight.Thin,

        hasGlow          = false,
        glowColor        = Color.Transparent,

        historyCardBg    = Color(0xFFFFFFFF),
        historyAccent    = Color(0xFF1A1A1A),

        tabSelected      = Color(0xFF1A1A1A),
        tabUnselected    = Color.Transparent,
        tabTextSelected  = Color(0xFFFFFFFF),
        tabTextUnselected= Color(0xFFBBBBB0),
        tabBorder        = Color(0xFF1A1A1A),

        liveIndicatorColor = Color(0xFF888880),

        chipBg     = Color(0xFFF0F0EC),
        chipBorder = Color(0xFFCCCCC8),
        chipText   = Color(0xFF444440),
    )

    val PLAYFUL = CalcTheme(
        id = CalcThemeId.PLAYFUL,
        displayName = "Playful",
        emoji = "★",

        background       = Color(0xFFFFEEF5),
        displayBg        = Color(0xFFFFFFFF),
        surfaceCard      = Color(0xFFFFFFFF),

        borderColor      = Color(0xFFFFB3D1),
        borderWidth      = 2.dp,

        btnNumber        = Color(0xFFFFFFFF),
        btnOperator      = Color(0xFFFFE0F0),
        btnEquals        = Color(0xFFFF4D8D),
        btnSpecial       = Color(0xFFE8F4FF),
        btnScientific    = Color(0xFFF0EAFF),

        btnCornerRadius  = 24.dp,
        btnBorderColor   = Color(0xFFFFB3D1),
        btnBorderWidth   = 1.5.dp,

        textDisplay      = Color(0xFF2D2D3A),
        textExpression   = Color(0xFFBB88AA),
        textBtnNumber    = Color(0xFF2D2D3A),
        textBtnOperator  = Color(0xFFFF4D8D),
        textBtnEquals    = Color(0xFFFFFFFF),
        textBtnSpecial   = Color(0xFF4DA6FF),
        textBtnScientific= Color(0xFF9B6BFF),
        textAccent       = Color(0xFFFF4D8D),
        textDim          = Color(0xFFDDAACc),

        displayFontWeight = FontWeight.Medium,

        hasGlow          = true,
        glowColor        = Color(0x30FF4D8D),

        historyCardBg    = Color(0xFFFFFFFF),
        historyAccent    = Color(0xFFFF4D8D),

        tabSelected      = Color(0xFFFF4D8D),
        tabUnselected    = Color.Transparent,
        tabTextSelected  = Color(0xFFFFFFFF),
        tabTextUnselected= Color(0xFFDDAACC),
        tabBorder        = Color(0xFFFF4D8D),

        liveIndicatorColor = Color(0xFFFF4D8D),

        chipBg     = Color(0xFFFFE8F3),
        chipBorder = Color(0xFFFFB3D1),
        chipText   = Color(0xFFFF4D8D),
    )

    val all = listOf(DARK_LUXURY, BRUTALIST, MINIMAL, PLAYFUL)
    fun find(id: CalcThemeId) = all.first { it.id == id }
}
