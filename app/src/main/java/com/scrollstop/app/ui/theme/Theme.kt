package com.scrollstop.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ScrollStopPrimary,
    onPrimary = Color.White,
    primaryContainer = ScrollStopPrimaryLight,
    onPrimaryContainer = Color.White,
    secondary = ScrollStopSecondary,
    onSecondary = Color.White,
    secondaryContainer = ScrollStopSecondaryLight,
    onSecondaryContainer = Color.White,
    tertiary = ScrollStopTertiary,
    onTertiary = Color.White,
    background = GradientLightStart,
    onBackground = Color(0xFF1C1B1F),
    surface = SurfaceLight,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = CardLight,
    onSurfaceVariant = Color(0xFF49454F),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = ScrollStopPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = ScrollStopPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = ScrollStopSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = ScrollStopSecondary,
    onSecondaryContainer = Color(0xFFB0BEC5),
    tertiary = ScrollStopTertiary,
    onTertiary = Color.White,
    background = GradientDarkStart,
    onBackground = Color(0xFFE6E1E5),
    surface = SurfaceDark,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = CardDark,
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun ScrollStopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ScrollStopTypography,
        shapes = ScrollStopShapes,
        content = content
    )
}
