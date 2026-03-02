package com.example.calculator.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalcTheme
import com.example.calculator.ui.theme.CalcThemeId

enum class BtnType { NUMBER, OPERATOR, EQUALS, SPECIAL, SCIENTIFIC }

fun classifyKey(key: String, isScientificRow: Boolean = false): BtnType = when {
    key == "="                                           -> BtnType.EQUALS
    key in listOf("+", "-", "×", "÷", "^")             -> BtnType.OPERATOR
    key in listOf("C", "CE", "⌫", "±", "%")            -> BtnType.SPECIAL
    isScientificRow                                      -> BtnType.SCIENTIFIC
    else                                                 -> BtnType.NUMBER
}

@Composable
fun CalcButton(
    key: String,
    theme: CalcTheme,
    modifier: Modifier = Modifier,
    btnType: BtnType = BtnType.NUMBER,
    isWide: Boolean = false,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 600f),
        label = "btn_scale"
    )

    val bgColor = when (btnType) {
        BtnType.EQUALS     -> theme.btnEquals
        BtnType.OPERATOR   -> theme.btnOperator
        BtnType.SPECIAL    -> theme.btnSpecial
        BtnType.SCIENTIFIC -> theme.btnScientific
        BtnType.NUMBER     -> theme.btnNumber
    }

    val textColor = when (btnType) {
        BtnType.EQUALS     -> theme.textBtnEquals
        BtnType.OPERATOR   -> theme.textBtnOperator
        BtnType.SPECIAL    -> theme.textBtnSpecial
        BtnType.SCIENTIFIC -> theme.textBtnScientific
        BtnType.NUMBER     -> theme.textBtnNumber
    }

    val shape = RoundedCornerShape(theme.btnCornerRadius)

    val shadowElevation = when {
        theme.id == CalcThemeId.DARK_LUXURY && btnType == BtnType.EQUALS -> 12.dp
        theme.id == CalcThemeId.PLAYFUL -> 4.dp
        else -> 0.dp
    }

    val fontSize = when {
        key.length > 5 -> 11.sp
        key.length > 3 -> 13.sp
        else -> if (theme.id == CalcThemeId.BRUTALIST) 20.sp else 17.sp
    }

    val fontWeight = when {
        theme.id == CalcThemeId.BRUTALIST -> FontWeight.Black
        btnType == BtnType.EQUALS         -> FontWeight.Bold
        btnType == BtnType.OPERATOR       -> FontWeight.SemiBold
        else                              -> FontWeight.Normal
    }

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(shadowElevation, shape)
            .clip(shape)
            .background(
                if (theme.id == CalcThemeId.DARK_LUXURY && btnType == BtnType.EQUALS)
                    Brush.verticalGradient(listOf(
                        Color(0xFFEED070), Color(0xFFD4A853), Color(0xFFB8882E)
                    ))
                else Brush.verticalGradient(listOf(bgColor, bgColor))
            )
            .border(theme.btnBorderWidth, theme.btnBorderColor, shape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { pressed = true; tryAwaitRelease(); pressed = false },
                    onTap = { onClick() },
                    onLongPress = { onLongClick?.invoke() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = key,
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}