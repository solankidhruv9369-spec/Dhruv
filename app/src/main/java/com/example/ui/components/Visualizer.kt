package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import com.example.data.model.AppState
import com.example.ui.theme.ZoyaAmber
import com.example.ui.theme.ZoyaCyan
import com.example.ui.theme.ZoyaEmerald
import com.example.ui.theme.ZoyaPink
import com.example.ui.theme.ZoyaPurple
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Visualizer(
    state: AppState,
    audioLevel: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer_anim")

    // Continuous rotation
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Breathing pulse
    val breathingPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Fast oscillation for processing & speaking
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("companion_visualizer"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 3.8f)

            when (state) {
                AppState.IDLE -> {
                    // Gentle glowing ambient orb
                    val currentRadius = baseRadius * breathingPulse

                    // Ambient outer glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ZoyaCyan.copy(alpha = 0.25f),
                                ZoyaPurple.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = currentRadius * 1.6f
                        ),
                        radius = currentRadius * 1.6f,
                        center = center
                    )

                    // Rotating delicate orbital rings
                    val ring1Angle = Math.toRadians(rotationAngle.toDouble())
                    val ring2Angle = Math.toRadians((-rotationAngle * 0.7f).toDouble())

                    drawCircle(
                        color = ZoyaCyan.copy(alpha = 0.35f),
                        radius = currentRadius * 1.25f,
                        center = center,
                        style = Stroke(width = 2.5f)
                    )

                    drawCircle(
                        color = ZoyaPurple.copy(alpha = 0.3f),
                        radius = currentRadius * 1.45f,
                        center = center,
                        style = Stroke(width = 1.5f)
                    )

                    // Core luminous sphere
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.95f),
                                ZoyaCyan.copy(alpha = 0.85f),
                                ZoyaPurple.copy(alpha = 0.6f),
                                ZoyaPurple.copy(alpha = 0.1f)
                            ),
                            center = center,
                            radius = currentRadius
                        ),
                        radius = currentRadius,
                        center = center
                    )

                    // Satellite nodes
                    val satRadius = 6f
                    val satX = center.x + (currentRadius * 1.25f) * cos(ring1Angle).toFloat()
                    val satY = center.y + (currentRadius * 1.25f) * sin(ring1Angle).toFloat()
                    drawCircle(color = ZoyaCyan, radius = satRadius, center = Offset(satX, satY))
                }

                AppState.LISTENING -> {
                    // Reactive microphone amplitude rings
                    val amplifiedLevel = (audioLevel * 1.2f).coerceIn(0f, 1f)
                    val micRadius = baseRadius * (1f + amplifiedLevel * 0.45f)

                    // Expanding sound wave echo rings
                    val echoAlpha1 = (0.6f - amplifiedLevel * 0.3f).coerceIn(0.1f, 0.6f)
                    val echoAlpha2 = (0.4f - amplifiedLevel * 0.2f).coerceIn(0.05f, 0.4f)

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ZoyaEmerald.copy(alpha = 0.4f + amplifiedLevel * 0.3f),
                                ZoyaCyan.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = micRadius * 1.8f
                        ),
                        radius = micRadius * 1.8f,
                        center = center
                    )

                    drawCircle(
                        color = ZoyaEmerald.copy(alpha = echoAlpha1),
                        radius = micRadius * 1.35f,
                        center = center,
                        style = Stroke(width = 3f + amplifiedLevel * 4f)
                    )

                    drawCircle(
                        color = ZoyaCyan.copy(alpha = echoAlpha2),
                        radius = micRadius * 1.6f,
                        center = center,
                        style = Stroke(width = 2f + amplifiedLevel * 2f)
                    )

                    // Core emerald listening orb
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                ZoyaEmerald,
                                ZoyaCyan.copy(alpha = 0.7f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = micRadius
                        ),
                        radius = micRadius,
                        center = center
                    )
                }

                AppState.PROCESSING -> {
                    // Rotating multi-arc spinner of intelligence
                    val procRadius = baseRadius * 1.1f

                    // Soft purple glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ZoyaCyan.copy(alpha = 0.3f),
                                ZoyaPurple.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = procRadius * 1.5f
                        ),
                        radius = procRadius * 1.5f,
                        center = center
                    )

                    // Arc 1 (Cyan)
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(Color.Transparent, ZoyaCyan, Color.White),
                            center = center
                        ),
                        startAngle = rotationAngle * 1.8f,
                        sweepAngle = 260f,
                        useCenter = false,
                        topLeft = Offset(center.x - procRadius, center.y - procRadius),
                        size = Size(procRadius * 2, procRadius * 2),
                        style = Stroke(width = 6f, cap = StrokeCap.Round)
                    )

                    // Arc 2 (Pink/Purple)
                    val innerRadius = procRadius * 0.75f
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(Color.Transparent, ZoyaPink, ZoyaPurple),
                            center = center
                        ),
                        startAngle = -rotationAngle * 2.2f,
                        sweepAngle = 200f,
                        useCenter = false,
                        topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                        size = Size(innerRadius * 2, innerRadius * 2),
                        style = Stroke(width = 4.5f, cap = StrokeCap.Round)
                    )

                    // Center pulsing core dot
                    val corePulse = (sin(wavePhase) * 6f) + 14f
                    drawCircle(
                        color = Color.White,
                        radius = corePulse,
                        center = center
                    )
                }

                AppState.SPEAKING -> {
                    // Vibrant harmonic soundwave frequencies
                    val speakRadius = baseRadius * 1.1f

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ZoyaPink.copy(alpha = 0.35f),
                                ZoyaPurple.copy(alpha = 0.25f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = speakRadius * 1.7f
                        ),
                        radius = speakRadius * 1.7f,
                        center = center
                    )

                    // Draw dynamic frequency bars around circle
                    val numBars = 32
                    for (i in 0 until numBars) {
                        val angle = (i.toFloat() / numBars) * (2 * PI).toFloat()
                        // Modulate height with harmonic sine waves
                        val waveMod = sin(angle * 3 + wavePhase) * cos(angle * 2 - wavePhase * 1.5f)
                        val barHeight = (18f + waveMod * 32f).coerceAtLeast(6f)

                        val startDist = speakRadius * 0.85f
                        val endDist = startDist + barHeight

                        val startX = center.x + startDist * cos(angle)
                        val startY = center.y + startDist * sin(angle)
                        val endX = center.x + endDist * cos(angle)
                        val endY = center.y + endDist * sin(angle)

                        val barColor = if (i % 2 == 0) ZoyaPink else ZoyaPurple

                        drawLine(
                            color = barColor,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 4f,
                            cap = StrokeCap.Round
                        )
                    }

                    // Inner bright core
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                ZoyaPink.copy(alpha = 0.9f),
                                ZoyaPurple.copy(alpha = 0.7f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = speakRadius * 0.7f
                        ),
                        radius = speakRadius * 0.7f,
                        center = center
                    )
                }

                AppState.RECONNECTING -> {
                    // Pulsing amber alert orb
                    val reconRadius = baseRadius * breathingPulse

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ZoyaAmber.copy(alpha = 0.4f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = reconRadius * 1.5f
                        ),
                        radius = reconRadius * 1.5f,
                        center = center
                    )

                    drawCircle(
                        color = ZoyaAmber,
                        radius = reconRadius,
                        center = center,
                        style = Stroke(width = 4f)
                    )

                    drawCircle(
                        color = ZoyaAmber.copy(alpha = 0.7f),
                        radius = reconRadius * 0.5f,
                        center = center
                    )
                }
            }
        }
    }
}
