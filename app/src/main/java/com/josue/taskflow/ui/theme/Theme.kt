package com.josue.taskflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Color.White,
    secondary = VerdeSecundario,
    background = FondoClaro,
    surface = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulOscuro,
    secondary = VerdeOscuro,
    background = FondoOscuro
)

@Composable
fun TaskFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colores = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colores,
        typography = Typography,
        content = content
    )
}
