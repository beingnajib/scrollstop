package com.scrollstop.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.scrollstop.app.ui.theme.ScrollStopPrimary
import com.scrollstop.app.ui.theme.ScrollStopSecondary
import com.scrollstop.app.ui.theme.ShieldBlue
import com.scrollstop.app.ui.theme.StarGold
import com.scrollstop.app.ui.theme.SuccessGreen

enum class MascotState { HAPPY, ALERT, CELEBRATING }

@Composable
fun ShieldMascot(
    state: MascotState,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascot")

    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (state == MascotState.HAPPY) 8f else if (state == MascotState.CELEBRATING) 12f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (state == MascotState.ALERT) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val shieldColor = when (state) {
        MascotState.HAPPY -> ShieldBlue
        MascotState.ALERT -> ScrollStopPrimary
        MascotState.CELEBRATING -> SuccessGreen
    }

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width * pulse
        val h = this.size.height * pulse
        val offsetX = (this.size.width - w) / 2
        val offsetY = (this.size.height - h) / 2 - bounce

        // Shield body
        drawShieldPath(offsetX, offsetY, w, h, shieldColor)

        // Eyes
        val eyeY = offsetY + h * 0.38f
        val eyeSize = w * 0.07f
        val eyeSpacing = w * 0.12f
        val centerX = offsetX + w / 2

        val eyeColor = Color.White
        // Left eye
        drawCircle(eyeColor, eyeSize, Offset(centerX - eyeSpacing, eyeY))
        // Right eye
        drawCircle(eyeColor, eyeSize, Offset(centerX + eyeSpacing, eyeY))

        // Pupils
        val pupilSize = eyeSize * 0.55f
        val pupilColor = Color(0xFF2D2D2D)
        drawCircle(pupilColor, pupilSize, Offset(centerX - eyeSpacing, eyeY))
        drawCircle(pupilColor, pupilSize, Offset(centerX + eyeSpacing, eyeY))

        // Mouth
        when (state) {
            MascotState.HAPPY, MascotState.CELEBRATING -> {
                // Smile arc
                drawArc(
                    color = Color.White,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(centerX - w * 0.1f, offsetY + h * 0.47f),
                    size = Size(w * 0.2f, h * 0.1f)
                )
            }
            MascotState.ALERT -> {
                // Worried "O" mouth
                drawCircle(
                    Color.White,
                    w * 0.04f,
                    Offset(centerX, offsetY + h * 0.52f)
                )
            }
        }

        // Celebrating extras: star sparkles
        if (state == MascotState.CELEBRATING) {
            drawCircle(StarGold, w * 0.03f, Offset(offsetX + w * 0.15f, offsetY + h * 0.15f))
            drawCircle(StarGold, w * 0.025f, Offset(offsetX + w * 0.85f, offsetY + h * 0.2f))
            drawCircle(StarGold, w * 0.02f, Offset(offsetX + w * 0.1f, offsetY + h * 0.6f))
            drawCircle(StarGold, w * 0.035f, Offset(offsetX + w * 0.9f, offsetY + h * 0.55f))
        }
    }
}

private fun DrawScope.drawShieldPath(
    offsetX: Float,
    offsetY: Float,
    w: Float,
    h: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(offsetX + w * 0.5f, offsetY + h * 0.05f)
        // Top left curve
        cubicTo(
            offsetX + w * 0.2f, offsetY + h * 0.05f,
            offsetX + w * 0.05f, offsetY + h * 0.15f,
            offsetX + w * 0.05f, offsetY + h * 0.35f
        )
        // Left side
        lineTo(offsetX + w * 0.05f, offsetY + h * 0.5f)
        // Bottom left curve to point
        cubicTo(
            offsetX + w * 0.05f, offsetY + h * 0.7f,
            offsetX + w * 0.25f, offsetY + h * 0.85f,
            offsetX + w * 0.5f, offsetY + h * 0.95f
        )
        // Bottom right curve
        cubicTo(
            offsetX + w * 0.75f, offsetY + h * 0.85f,
            offsetX + w * 0.95f, offsetY + h * 0.7f,
            offsetX + w * 0.95f, offsetY + h * 0.5f
        )
        // Right side
        lineTo(offsetX + w * 0.95f, offsetY + h * 0.35f)
        // Top right curve
        cubicTo(
            offsetX + w * 0.95f, offsetY + h * 0.15f,
            offsetX + w * 0.8f, offsetY + h * 0.05f,
            offsetX + w * 0.5f, offsetY + h * 0.05f
        )
        close()
    }
    drawPath(path, color, style = Fill)
}
