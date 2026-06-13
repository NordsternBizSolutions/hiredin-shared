package com.nordstern.hiredin.shared.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = HiredInColors.Primary,
    onPrimary = HiredInColors.OnPrimary,
    secondary = HiredInColors.Secondary,
    background = HiredInColors.Background,
    surface = HiredInColors.Surface,
    surfaceVariant = HiredInColors.SurfaceVariant,
    error = HiredInColors.Error,
    onBackground = HiredInColors.OnBackground,
    onSurface = HiredInColors.OnSurface,
    onSurfaceVariant = HiredInColors.OnSurfaceVariant,
    outline = HiredInColors.Outline
)

private val DarkColorScheme = darkColorScheme(
    primary = HiredInDarkColors.Primary,
    onPrimary = HiredInColors.OnPrimary,
    secondary = HiredInColors.Secondary,
    background = HiredInDarkColors.Background,
    surface = HiredInDarkColors.Surface,
    surfaceVariant = HiredInDarkColors.SurfaceVariant,
    error = HiredInColors.Error,
    onBackground = HiredInDarkColors.OnBackground,
    onSurface = HiredInDarkColors.OnSurface,
    outline = HiredInDarkColors.Outline
)

data class ExtendedColors(
    val success: Color = HiredInColors.Success,
    val warning: Color = HiredInColors.Warning,
    val info: Color = HiredInColors.Info
)

val LocalExtendedColors = staticCompositionLocalOf { ExtendedColors() }

@Composable
fun HiredInTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = HiredInTypography,
        shapes = HiredInShapes,
        content = {
            CompositionLocalProvider(LocalExtendedColors provides ExtendedColors()) {
                content()
            }
        }
    )
}
