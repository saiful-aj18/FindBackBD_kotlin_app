package com.saiful.findbackbd.ui.screens.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val SplashBgGreen = Color(0xFF134E39)
private val SplashIconBoxGreen = Color(0xFF2B5F4C)
private val SplashMountainGreen = Color(0xFF215845)
private val SplashSubtitleColor = Color(0xFFD5E3DD)
private val SplashBarTrackColor = Color(0xFF2F584A)
private val SplashBarIndicatorColor = Color(0xFF638679)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2200)
        onFinished()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_loader")
    val indicatorBias by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "indicator_bias"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBgGreen)
            .testTag("splash_screen")
    ) {
        // Bottom geometric mountain silhouette
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val w = size.width
            val h = size.height

            val mountainPath = Path().apply {
                moveTo(0f, h * 0.796f)
                lineTo(w * 0.098f, h * 0.767f)
                lineTo(w * 0.150f, h * 0.696f)
                lineTo(w * 0.223f, h * 0.784f)
                lineTo(w * 0.296f, h * 0.733f)
                lineTo(w * 0.371f, h * 0.799f)
                lineTo(w * 0.447f, h * 0.748f)
                lineTo(w * 0.522f, h * 0.814f)
                lineTo(w * 0.597f, h * 0.763f)
                lineTo(w * 0.673f, h * 0.829f)
                lineTo(w * 0.748f, h * 0.778f)
                lineTo(w * 0.824f, h * 0.844f)
                lineTo(w * 0.898f, h * 0.793f)
                lineTo(w, h * 0.838f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }

            drawPath(
                path = mountainPath,
                color = SplashMountainGreen
            )
        }

        // Center Logo & Typography
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .offset(y = (-24).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(102.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(SplashIconBoxGreen),
                contentAlignment = Alignment.Center
            ) {
                OutlinedLocationPinIcon(
                    modifier = Modifier.size(48.dp),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "FindBack BD",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.3).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Lost & Found, Together",
                color = SplashSubtitleColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.2.sp
            )
        }

        // Bottom pill progress bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 36.dp)
                .width(114.dp)
                .height(4.5.dp)
                .clip(CircleShape)
                .background(SplashBarTrackColor)
        ) {
            val trackWidthDp = 114f
            val segmentWidthDp = 38f
            val offsetX = (trackWidthDp - segmentWidthDp) * indicatorBias
            Box(
                modifier = Modifier
                    .offset(x = offsetX.dp)
                    .width(segmentWidthDp.dp)
                    .height(4.5.dp)
                    .clip(CircleShape)
                    .background(SplashBarIndicatorColor)
            )
        }
    }
}

@Composable
private fun OutlinedLocationPinIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = w * 0.082f

        val cx = w * 0.5f
        val cy = h * 0.40f
        val topRadius = w * 0.33f
        val tipY = h * 0.88f

        // Outer location pin path
        val pinPath = Path().apply {
            moveTo(cx - topRadius, cy)
            arcTo(
                rect = Rect(
                    left = cx - topRadius,
                    top = cy - topRadius,
                    right = cx + topRadius,
                    bottom = cy + topRadius
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            cubicTo(
                cx + topRadius, cy + topRadius * 0.62f,
                cx + topRadius * 0.22f, h * 0.77f,
                cx, tipY
            )
            cubicTo(
                cx - topRadius * 0.22f, h * 0.77f,
                cx - topRadius, cy + topRadius * 0.62f,
                cx - topRadius, cy
            )
            close()
        }

        drawPath(
            path = pinPath,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Concentric inner circle
        drawCircle(
            color = color,
            radius = w * 0.125f,
            center = Offset(cx, cy),
            style = Stroke(width = strokeWidth)
        )
    }
}
