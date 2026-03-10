package com.scrollstop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.scrollstop.app.ui.theme.GradientDarkEnd
import com.scrollstop.app.ui.theme.GradientDarkStart
import com.scrollstop.app.ui.theme.GradientLightEnd
import com.scrollstop.app.ui.theme.GradientLightStart

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(
                colors = if (isDark) listOf(GradientDarkStart, GradientDarkEnd)
                else listOf(GradientLightStart, GradientLightEnd)
            )
        )
    ) {
        content()
    }
}
