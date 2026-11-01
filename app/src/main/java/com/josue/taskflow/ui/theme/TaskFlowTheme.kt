package com.josue.taskflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TealPrimaryLight,
    onPrimary = TealOnPrimaryLight,
    primaryContainer = TealPrimaryContainerLight,
    onPrimaryContainer = TealOnPrimaryContainerLight,
    secondary = TealSecondaryLight,
    onSecondary = TealOnSecondaryLight,
    secondaryContainer = TealSecondaryContainerLight,
    onSecondaryContainer = TealOnSecondaryContainerLight,
    tertiary = TealTertiaryLight,
    onTertiary = TealOnTertiaryLight,
    tertiaryContainer = TealTertiaryContainerLight,
    onTertiaryContainer = TealOnTertiaryContainerLight,
    background = TealBackgroundLight,
    onBackground = TealOnBackgroundLight,
    surface = TealSurfaceLight,
    onSurface = TealOnSurfaceLight,
    surfaceVariant = TealSurfaceVariantLight,
    onSurfaceVariant = TealOnSurfaceVariantLight,
    surfaceContainer = Color(0xFFEEEFEA),
    surfaceContainerLow = Color(0xFFF4F5F0),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFE8E9E4),
    surfaceContainerHighest = Color(0xFFE2E3DE),
    outline = TealOutlineLight,
    outlineVariant = TealOutlineVariantLight,
    error = TealErrorLight,
    onError = TealOnErrorLight,
    errorContainer = TealErrorContainerLight,
    onErrorContainer = TealOnErrorContainerLight,
    inverseSurface = TealInverseSurfaceLight,
    inverseOnSurface = TealInverseOnSurfaceLight,
    inversePrimary = TealInversePrimaryLight
)

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimaryDark,
    onPrimary = TealOnPrimaryDark,
    primaryContainer = TealPrimaryContainerDark,
    onPrimaryContainer = TealOnPrimaryContainerDark,
    secondary = TealSecondaryDark,
    onSecondary = TealOnSecondaryDark,
    secondaryContainer = TealSecondaryContainerDark,
    onSecondaryContainer = TealOnSecondaryContainerDark,
    tertiary = TealTertiaryDark,
    onTertiary = TealOnTertiaryDark,
    tertiaryContainer = TealTertiaryContainerDark,
    onTertiaryContainer = TealOnTertiaryContainerDark,
    background = TealBackgroundDark,
    onBackground = TealOnBackgroundDark,
    surface = TealSurfaceDark,
    onSurface = TealOnSurfaceDark,
    surfaceVariant = TealSurfaceVariantDark,
    onSurfaceVariant = TealOnSurfaceVariantDark,
    surfaceContainer = Color(0xFF1D2420),
    surfaceContainerLow = Color(0xFF19201C),
    surfaceContainerLowest = Color(0xFF0F1512),
    surfaceContainerHigh = Color(0xFF272E2A),
    surfaceContainerHighest = Color(0xFF323A35),
    outline = TealOutlineDark,
    outlineVariant = TealOutlineVariantDark,
    error = TealErrorDark,
    onError = TealOnErrorDark,
    errorContainer = TealErrorContainerDark,
    onErrorContainer = TealOnErrorContainerDark,
    inverseSurface = TealInverseSurfaceDark,
    inverseOnSurface = TealInverseOnSurfaceDark,
    inversePrimary = TealInversePrimaryDark
)

@Composable
fun TaskFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
