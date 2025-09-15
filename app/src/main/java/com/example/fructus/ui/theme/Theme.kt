package com.example.fructus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Create CompositionLocal for custom colors
val LocalAppColors = staticCompositionLocalOf { LightAppColors }

// Updated light scheme using your custom colors
private val lightScheme = lightColorScheme(
    primary = LightAppColors.main,
    onPrimary = Color.White,
    primaryContainer = LightAppColors.accent,
    onPrimaryContainer = LightAppColors.textPrimary,
    secondary = LightAppColors.accent,
    onSecondary = LightAppColors.textPrimary,
    background = LightAppColors.bg,
    onBackground = LightAppColors.textPrimary,
    surface = LightAppColors.surface,
    onSurface = LightAppColors.textPrimary,
    onSurfaceVariant = LightAppColors.ripenessStage,
    onSecondaryContainer = LightAppColors.outerBox,
    onTertiaryContainer = LightAppColors.innerBox,
    tertiaryContainer = LightAppColors.button,
    // Add more mappings as needed
)

// New dark scheme using your custom colors
private val darkScheme = darkColorScheme(
    primary = DarkAppColors.main,
    onPrimary = Color.Black,
    primaryContainer = DarkAppColors.accent,
    onPrimaryContainer = DarkAppColors.textPrimary,
    secondary = DarkAppColors.accent,
    onSecondary = DarkAppColors.textPrimary,
    background = DarkAppColors.bg,
    onBackground = DarkAppColors.textPrimary,
    surface = DarkAppColors.surface,
    onSurface = DarkAppColors.textPrimary,
    onSurfaceVariant = DarkAppColors.ripenessStage,
    onSecondaryContainer = DarkAppColors.outerBox,
    onTertiaryContainer = DarkAppColors.innerBox,
    tertiaryContainer = DarkAppColors.button,
    // Add more mappings as needed
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun FructusTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkScheme else lightScheme
    val appColors = if (darkTheme) DarkAppColors else LightAppColors

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

// Extension to access custom colors easily
val MaterialTheme.appColors: AppColors
    @Composable get() = LocalAppColors.current