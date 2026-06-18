package com.nordstern.hiredin.shared.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ExtendedColors(
    val success: Color = HiredInColors.Success,
    val warning: Color = HiredInColors.Warning,
    val info: Color = HiredInColors.Info
)

val LocalExtendedColors = staticCompositionLocalOf { ExtendedColors() }

private val DarkExtendedColors = ExtendedColors(
    success = HiredInDarkColors.Success,
    warning = HiredInColors.Warning,
    info = HiredInColors.Info
)

@Composable
fun HiredInTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) HiredInDarkColorScheme else HiredInLightColorScheme
    val extended = if (darkTheme) DarkExtendedColors else ExtendedColors()
    val adaptive = if (darkTheme) HiredInAdaptivePalette.dark else HiredInAdaptivePalette.light
    MaterialTheme(
        colorScheme = colorScheme,
        typography = HiredInTypography,
        shapes = HiredInShapes,
        content = {
            CompositionLocalProvider(
                LocalExtendedColors provides extended,
                LocalHiredInAdaptivePalette provides adaptive
            ) {
                content()
            }
        }
    )
}
