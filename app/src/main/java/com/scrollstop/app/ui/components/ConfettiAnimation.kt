package com.scrollstop.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.scrollstop.app.ui.theme.CelebrationPurple
import com.scrollstop.app.ui.theme.ScrollStopPrimary
import com.scrollstop.app.ui.theme.ScrollStopSecondary
import com.scrollstop.app.ui.theme.ScrollStopTertiary
import com.scrollstop.app.ui.theme.StarGold
import com.scrollstop.app.ui.theme.SuccessGreen
import kotlin.random.Random

private data class Particle(
    val x: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val delay: Float,
    val wobble: Float
)

@Composable
fun ConfettiOverlay(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 40
) {
    if (!isPlaying) return

    val colors = listOf(
        ScrollStopPrimary, ScrollStopSecondary, ScrollStopTertiary,
        StarGold, SuccessGreen, CelebrationPurple
    )

    val particles = remember {
        List(particleCount) {
            Particle(
                x = Random.nextFloat(),
                speed = 0.3f + Random.nextFloat() * 0.7f,
                size = 4f + Random.nextFloat() * 8f,
                color = colors[Random.nextInt(colors.size)],
                delay = Random.nextFloat(),
                wobble = Random.nextFloat() * 40f - 20f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "confettiProgress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val adjustedProgress = ((progress + particle.delay) % 1f)
            val y = -particle.size + adjustedProgress * (size.height + particle.size * 2)
            val x = particle.x * size.width + kotlin.math.sin(adjustedProgress * 6.28f) * particle.wobble

            drawRect(
                color = particle.color.copy(alpha = (1f - adjustedProgress).coerceIn(0.3f, 1f)),
                topLeft = Offset(x, y),
                size = Size(particle.size, particle.size * 1.5f)
            )
        }
    }
}
