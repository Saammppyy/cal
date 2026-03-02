package com.example.calculator.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalcTheme
import com.example.calculator.ui.theme.CalcThemeId
import com.example.calculator.viewmodel.CalculatorViewModel

@Composable
fun DisplayArea(
    vm: CalculatorViewModel,
    theme: CalcTheme,
    modifier: Modifier = Modifier
) {
    val isLive = vm.liveResult.isNotEmpty() && !vm.justEvaluated

    val displayText = when {
        vm.isError       -> vm.displayResult
        vm.justEvaluated -> vm.displayResult
        isLive           -> vm.liveResult
        vm.expression.isEmpty() -> vm.displayResult
        else             -> vm.displayResult
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(theme.displayBg)
            .border(
                width = theme.borderWidth,
                color = theme.borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .drawBehind {
                if (theme.hasGlow) {
                    drawGlow(theme.glowColor, size.width, size.height)
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, delta ->
                    if (delta < -30f) vm.backspace()
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.End
        ) {

            // Expression line
            AnimatedContent(
                targetState = vm.expression,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "expr_anim"
            ) { expr ->
                Text(
                    text = expr.ifEmpty { if (vm.justEvaluated) "  " else "0" },
                    color = theme.textExpression,
                    fontSize = 16.sp,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(6.dp))

            // Main number
            AnimatedContent(
                targetState = Pair(displayText, isLive),
                transitionSpec = {
                    (slideInVertically { it / 4 } + fadeIn()) togetherWith
                            (slideOutVertically { -it / 4 } + fadeOut())
                },
                label = "display_anim"
            ) { (text, live) ->
                val fs = when {
                    text.length > 16 -> 24.sp
                    text.length > 12 -> 32.sp
                    text.length > 8  -> 40.sp
                    text.length > 6  -> 46.sp
                    else             -> 52.sp
                }
                Text(
                    text = text,
                    color = when {
                        vm.isError -> Color(0xFFE05555)
                        live       -> theme.textExpression
                        else       -> theme.textDisplay
                    },
                    fontSize = fs,
                    fontWeight = theme.displayFontWeight,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Live indicator
            AnimatedVisibility(visible = isLive) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(theme.liveIndicatorColor)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "preview",
                        color = theme.liveIndicatorColor,
                        fontSize = 9.sp,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // Quick suggestions
            AnimatedVisibility(
                visible = vm.suggestions.isNotEmpty(),
                enter = slideInVertically { it } + fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    vm.suggestions.forEach { (label, value) ->
                        SuggestionChip(
                            label = label,
                            value = value,
                            theme = theme
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                }
            }

            // Programmer info
            if (vm.mode == com.example.calculator.viewmodel.CalcMode.PROGRAMMER && vm.programmerValue != null) {
                Spacer(Modifier.height(10.dp))
                Divider(color = theme.borderColor)
                Spacer(Modifier.height(8.dp))
                listOf("HEX" to vm.getHex(), "BIN" to vm.getBinary(), "OCT" to vm.getOctal()).forEach { (lbl, v) ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(lbl, color = theme.textBtnSpecial, fontSize = 10.sp, letterSpacing = 1.5.sp)
                        Text(v, color = theme.textExpression, fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }
                    Spacer(Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
fun SuggestionChip(label: String, value: String, theme: CalcTheme) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(theme.chipBg)
            .border(0.5.dp, theme.chipBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, color = theme.chipText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text("=", color = theme.chipText.copy(alpha = 0.5f), fontSize = 9.sp)
        Text(value, color = theme.chipText, fontSize = 9.sp)
    }
}

@Composable
fun Divider(color: Color) {
    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(color))
}

private fun DrawScope.drawGlow(glowColor: Color, w: Float, h: Float) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(glowColor, Color.Transparent),
            center = Offset(w * 0.85f, h * 0.2f),
            radius = w * 0.5f
        ),
        radius = w * 0.5f,
        center = Offset(w * 0.85f, h * 0.2f)
    )
}
