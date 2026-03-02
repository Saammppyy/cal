package com.example.calculator

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ─── Colour palette from the icon ────────────────────────────────────────────
private val NavyDark   = Color(0xFF1C2232)   // top-left quadrant
private val PureBlack  = Color(0xFF0A0A0A)   // top-right / main bg
private val BrownDark  = Color(0xFF1E1208)   // bottom-left quadrant
private val Cream      = Color(0xFFF2ECD8)   // bottom-right quadrant
private val Gold       = Color(0xFFCB9B4E)   // + × operators
private val RedDeep    = Color(0xFFD04040)   // ÷ dot and bar
private val RedSoft    = Color(0xFFE87070)   // = bars

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashScreen(onFinished = {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            })
        }
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    // ── Animation states ──────────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "ambient")

    // Subtle rotating highlight ring
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "ring_rot"
    )

    // Orchestrated entrance — all driven from a single elapsed timer
    var phase by remember { mutableStateOf(0) }  // 0=idle 1=quadrants 2=icon 3=text 4=tagline 5=exit

    val quadrantAlpha    = remember { Animatable(0f) }
    val quadrantScale    = remember { Animatable(0.88f) }
    val iconScale        = remember { Animatable(0f) }
    val iconAlpha        = remember { Animatable(0f) }
    val textAlpha        = remember { Animatable(0f) }
    val textTranslateY   = remember { Animatable(18f) }
    val tagAlpha         = remember { Animatable(0f) }
    val screenAlpha      = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Phase 1 — background quadrants fade + scale in
        delay(80)
        quadrantAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        quadrantScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        phase = 1

        // Phase 2 — icon pops in with spring
        delay(120)
        iconAlpha.animateTo(1f, tween(260))
        iconScale.animateTo(1f, spring(dampingRatio = 0.52f, stiffness = 280f))
        phase = 2

        // Phase 3 — app name slides up
        delay(180)
        textAlpha.animateTo(1f, tween(340, easing = FastOutSlowInEasing))
        textTranslateY.animateTo(0f, tween(340, easing = FastOutSlowInEasing))
        phase = 3

        // Phase 4 — tagline fades
        delay(200)
        tagAlpha.animateTo(1f, tween(300))
        phase = 4

        // Hold
        delay(1100)

        // Phase 5 — fade out to MainActivity
        screenAlpha.animateTo(0f, tween(380, easing = FastOutSlowInEasing))
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(screenAlpha.value)
            .background(PureBlack),
        contentAlignment = Alignment.Center
    ) {

        // ── Ambient rotating arc behind everything ────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawAmbientRing(ringRotation, size.width, size.height)
        }

        // ── Background quadrant grid (mirrors the icon) ───────────────────────
        Canvas(
            modifier = Modifier
                .size(260.dp)
                .alpha(quadrantAlpha.value)
                .scale(quadrantScale.value)
                .graphicsLayer { shadowElevation = 80f }
        ) {
            drawIconQuadrants(size)
        }

        // ── Foreground symbol layer (the operators from the icon) ─────────────
        Canvas(
            modifier = Modifier
                .size(260.dp)
                .scale(iconScale.value)
                .alpha(iconAlpha.value)
        ) {
            drawOperatorSymbols(size)
        }

        // ── Text block ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App name
            Text(
                text = "カルキュレーター",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 12.sp,
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .graphicsLayer { translationY = textTranslateY.value * density }
            )
            Spacer(Modifier.height(8.dp))
            // Gold divider line
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(1.5.dp)
                    .alpha(textAlpha.value)
                    .background(Gold)
            )
            Spacer(Modifier.height(12.dp))
            // Tagline
            Text(
                text = "Calculate in Style",
                color = Gold.copy(alpha = 0.80f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(tagAlpha.value)
            )
        }

        // ── Version badge bottom-right ────────────────────────────────────────
        Text(
            text = "v1.0",
            color = Color.White.copy(alpha = 0.18f),
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .alpha(tagAlpha.value)
        )
    }
}

// ─── Draw the 4 quadrants exactly like the icon ──────────────────────────────
private fun DrawScope.drawIconQuadrants(size: Size) {
    val half = size.width / 2f
    val r = size.width * 0.13f   // corner softness for inner join

    // Top-left — navy
    drawRect(NavyDark,   topLeft = Offset(0f, 0f),      size = Size(half - 3f, half - 3f))
    // Top-right — near black
    drawRect(PureBlack,  topLeft = Offset(half + 3f, 0f), size = Size(half - 3f, half - 3f))
    // Bottom-left — dark brown
    drawRect(BrownDark,  topLeft = Offset(0f, half + 3f), size = Size(half - 3f, half - 3f))
    // Bottom-right — cream
    drawRect(Cream,      topLeft = Offset(half + 3f, half + 3f), size = Size(half - 3f, half - 3f))
}

// ─── Draw the operator symbols layered on top ─────────────────────────────────
private fun DrawScope.drawOperatorSymbols(size: Size) {
    val half  = size.width / 2f
    val cx    = half          // centre x
    val cy    = half          // centre y
    val unit  = size.width * 0.065f   // base stroke unit

    // ── Top-right: "0" display (gold oval outline) ────────────────────────────
    val ovalCx = cx + half * 0.5f
    val ovalCy = cy - half * 0.5f
    drawOval(
        color = Gold,
        topLeft = Offset(ovalCx - unit * 1.1f, ovalCy - unit * 1.55f),
        size   = Size(unit * 2.2f, unit * 3.1f),
        style  = androidx.compose.ui.graphics.drawscope.Stroke(width = unit * 0.55f)
    )

    // ── Bottom-right top half: red ÷ dot + bar ────────────────────────────────
    val divCx = cx + half * 0.5f
    val dotY  = cy + half * 0.10f
    val barY  = cy + half * 0.38f
    // dot
    drawCircle(RedDeep, radius = unit * 0.38f, center = Offset(divCx, dotY))
    // bar
    drawRoundRect(
        color     = RedDeep,
        topLeft   = Offset(divCx - unit * 1.15f, barY - unit * 0.22f),
        size      = Size(unit * 2.3f, unit * 0.45f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(unit * 0.22f)
    )

    // ── Bottom-right bottom: = bars (coral/soft red) ──────────────────────────
    val eqCx  = cx + half * 0.5f
    val bar1Y = cy + half * 0.65f
    val bar2Y = cy + half * 0.82f
    for (bY in listOf(bar1Y, bar2Y)) {
        drawRoundRect(
            color     = RedSoft,
            topLeft   = Offset(eqCx - unit * 1.15f, bY - unit * 0.22f),
            size      = Size(unit * 2.3f, unit * 0.42f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(unit * 0.21f)
        )
    }

    // ── Bottom-left: + symbol (gold) ──────────────────────────────────────────
    val plusCx = cx - half * 0.58f
    val plusCy = cy + half * 0.26f
    drawRoundRect(Gold, Offset(plusCx - unit * 0.22f, plusCy - unit), Size(unit * 0.44f, unit * 2f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(unit * 0.15f))
    drawRoundRect(Gold, Offset(plusCx - unit, plusCy - unit * 0.22f), Size(unit * 2f, unit * 0.44f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(unit * 0.15f))

    // ── Bottom-left: − symbol (gold) ─────────────────────────────────────────
    val minusCx = cx - half * 0.18f
    val minusCy = plusCy
    drawRoundRect(Gold, Offset(minusCx - unit, minusCy - unit * 0.22f), Size(unit * 2f, unit * 0.44f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(unit * 0.15f))

    // ── Bottom-left: × symbol (gold) ─────────────────────────────────────────
    val mulCx = plusCx
    val mulCy = cy + half * 0.68f
    rotate(45f, pivot = Offset(mulCx, mulCy)) {
        drawRoundRect(Gold, Offset(mulCx - unit * 0.22f, mulCy - unit), Size(unit * 0.44f, unit * 2f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(unit * 0.15f))
        drawRoundRect(Gold, Offset(mulCx - unit, mulCy - unit * 0.22f), Size(unit * 2f, unit * 0.44f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(unit * 0.15f))
    }

    // ── Bottom-left: ÷ symbol (gold, small) ──────────────────────────────────
    val divSmCx = minusCx
    val divSmCy = mulCy
    drawCircle(Gold, radius = unit * 0.22f, center = Offset(divSmCx, divSmCy - unit * 0.7f))
    drawRoundRect(Gold, Offset(divSmCx - unit, divSmCy - unit * 0.22f), Size(unit * 2f, unit * 0.44f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(unit * 0.15f))
    drawCircle(Gold, radius = unit * 0.22f, center = Offset(divSmCx, divSmCy + unit * 0.7f))
}

// ─── Ambient rotating arc in the background ───────────────────────────────────
private fun DrawScope.drawAmbientRing(rotation: Float, w: Float, h: Float) {
    val cx = w / 2f
    val cy = h * 0.38f
    val r  = w * 0.52f
    rotate(rotation, pivot = Offset(cx, cy)) {
        drawArc(
            color      = Gold.copy(alpha = 0.06f),
            startAngle = 0f,
            sweepAngle = 240f,
            useCenter  = false,
            topLeft    = Offset(cx - r, cy - r),
            size       = Size(r * 2, r * 2),
            style      = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
        )
    }
    rotate(-rotation * 0.6f, pivot = Offset(cx, cy + h * 0.1f)) {
        drawArc(
            color      = RedSoft.copy(alpha = 0.04f),
            startAngle = 60f,
            sweepAngle = 180f,
            useCenter  = false,
            topLeft    = Offset(cx - r * 0.7f, cy - r * 0.7f + h * 0.1f),
            size       = Size(r * 1.4f, r * 1.4f),
            style      = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
        )
    }
}
