package com.example.calculator.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalcTheme
import com.example.calculator.ui.theme.CalcThemeId
import com.example.calculator.ui.theme.CalcThemes

@Composable
fun ThemePicker(
    currentThemeId: CalcThemeId,
    containerTheme: CalcTheme,
    onSelect: (CalcThemeId) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerTheme.displayBg)
            .padding(vertical = 12.dp)
    ) {
        Text(
            "THEME",
            color = containerTheme.textAccent,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcThemes.all.forEach { theme ->
                val isSelected = theme.id == currentThemeId
                val bgAnim by animateColorAsState(
                    if (isSelected) theme.btnEquals else containerTheme.btnNumber,
                    spring(), label = "theme_bg"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.85f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgAnim)
                        .border(
                            width = if (isSelected) 2.dp else 0.5.dp,
                            color = if (isSelected) theme.btnEquals else containerTheme.borderColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelect(theme.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            theme.emoji,
                            fontSize = 20.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            theme.displayName.uppercase(),
                            color = if (isSelected) theme.textBtnEquals else containerTheme.textExpression,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
