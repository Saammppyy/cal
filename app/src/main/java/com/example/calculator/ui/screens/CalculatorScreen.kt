package com.example.calculator.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculator.ui.components.*
import com.example.calculator.ui.theme.CalcThemes
import com.example.calculator.viewmodel.CalcMode
import com.example.calculator.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(vm: CalculatorViewModel = viewModel()) {
    val theme = CalcThemes.find(vm.themeId)
    var showHistory by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }

    // Animated background color on theme change
    val bgColor by animateColorAsState(
        targetValue = theme.background,
        animationSpec = tween(400),
        label = "bg_color"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Ambient glow canvas (dark luxury & playful only)
        if (theme.hasGlow) {
            AmbientGlowCanvas(theme.glowColor)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // ── Top Bar ─────────────────────────────────────────────────────
            TopBar(
                theme = theme,
                mode = vm.mode,
                angleMode = vm.angleMode,
                onHistoryOpen = { showHistory = true },
                onThemeOpen = { showThemePicker = !showThemePicker },
                onAngleToggle = vm::toggleAngleMode
            )

            // ── Theme Picker ─────────────────────────────────────────────────
            AnimatedVisibility(
                visible = showThemePicker,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                ThemePicker(
                    currentThemeId = vm.themeId,
                    containerTheme = theme,
                    onSelect = { id -> vm.setTheme(id); showThemePicker = false }
                )
            }

            // ── Display ──────────────────────────────────────────────────────
            DisplayArea(
                vm = vm,
                theme = theme,
                modifier = Modifier.weight(1f)
            )

            // ── Mode Tabs ────────────────────────────────────────────────────
            ModeTabs(vm = vm, theme = theme)

            // ── Keypad with mode transition ──────────────────────────────────
            AnimatedContent(
                targetState = vm.mode,
                transitionSpec = {
                    val forward = targetState.ordinal > initialState.ordinal
                    slideInHorizontally { if (forward) it else -it } + fadeIn() togetherWith
                            slideOutHorizontally { if (forward) -it else it } + fadeOut()
                },
                label = "mode_transition"
            ) { mode ->
                when (mode) {
                    CalcMode.BASIC       -> BasicKeypad(vm, theme)
                    CalcMode.SCIENTIFIC  -> ScientificKeypad(vm, theme)
                    CalcMode.PROGRAMMER  -> ProgrammerKeypad(vm, theme)
                }
            }

            Spacer(Modifier.height(8.dp))
        }

        // ── History Slide-Over ───────────────────────────────────────────────
        AnimatedVisibility(
            visible = showHistory,
            enter = slideInHorizontally { it } + fadeIn(tween(200)),
            exit = slideOutHorizontally { it } + fadeOut(tween(200))
        ) {
            HistoryPanel(
                history = vm.history,
                theme = theme,
                onUse = { vm.useHistoryEntry(it); showHistory = false },
                onClear = vm::clearHistory,
                onClose = { showHistory = false }
            )
        }
    }
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@Composable
fun TopBar(
    theme: com.example.calculator.ui.theme.CalcTheme,
    mode: CalcMode,
    angleMode: String,
    onHistoryOpen: () -> Unit,
    onThemeOpen: () -> Unit,
    onAngleToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App name / theme toggle
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(theme.chipBg)
                .border(theme.btnBorderWidth, theme.chipBorder, RoundedCornerShape(20.dp))
                .clickable(onClick = onThemeOpen)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(theme.emoji, fontSize = 12.sp)
                Text(
                    "CALC",
                    color = theme.textAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (mode == CalcMode.SCIENTIFIC) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(theme.chipBg)
                        .border(theme.btnBorderWidth, theme.chipBorder, RoundedCornerShape(20.dp))
                        .clickable(onClick = onAngleToggle)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(angleMode, color = theme.textBtnSpecial, fontSize = 10.sp, letterSpacing = 1.sp)
                }
            }
            SmallChip("⏱", theme, onClick = onHistoryOpen)
        }
    }
}

// ─── Mode Tabs ───────────────────────────────────────────────────────────────

@Composable
fun ModeTabs(vm: CalculatorViewModel, theme: com.example.calculator.ui.theme.CalcTheme) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(theme.surfaceCard)
            .border(theme.borderWidth, theme.borderColor, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CalcMode.values().forEach { mode ->
            val selected = vm.mode == mode
            val tabBg by animateColorAsState(
                if (selected) theme.tabSelected else theme.tabUnselected,
                tween(200), label = "tab_bg"
            )
            val tabTextColor by animateColorAsState(
                if (selected) theme.tabTextSelected else theme.tabTextUnselected,
                tween(200), label = "tab_text"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tabBg)
                    .border(
                        width = if (selected) theme.btnBorderWidth else 0.dp,
                        color = if (selected) theme.tabBorder else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { vm.updateMode(mode) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    mode.name,
                    color = tabTextColor,
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

// ─── Ambient Glow ────────────────────────────────────────────────────────────

@Composable
fun AmbientGlowCanvas(glowColor: Color) {
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(glowColor, Color.Transparent),
                center = Offset(size.width * 0.15f, size.height * 0.25f),
                radius = size.width * 0.55f
            ),
            radius = size.width * 0.55f,
            center = Offset(size.width * 0.15f, size.height * 0.25f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(glowColor.copy(alpha = 0.5f), Color.Transparent),
                center = Offset(size.width * 0.9f, size.height * 0.75f),
                radius = size.width * 0.4f
            ),
            radius = size.width * 0.4f,
            center = Offset(size.width * 0.9f, size.height * 0.75f)
        )
    }
}
