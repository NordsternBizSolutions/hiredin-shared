package com.nordstern.hiredin.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1A56DB),
    onPrimary = Color.White,
    secondary = Color(0xFF0E9F6E),
    background = Color(0xFFF9FAFB),
    surface = Color.White,
    error = Color(0xFFE02424)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF3B82F6),
    onPrimary = Color.White,
    secondary = Color(0xFF10B981),
    background = Color(0xFF111827),
    surface = Color(0xFF1F2937),
    error = Color(0xFFF87171)
)

@Composable
fun HiredInTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = HiredInTypography,
        content = content
    )
}
