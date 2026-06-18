package com.nordstern.hiredin.shared.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Theme-aware semantic colors for components that need grey scale and brand accents.
 * Use [hiredInColors] inside @Composable instead of static [HiredInColors] for UI surfaces/text.
 */
data class HiredInAdaptivePalette(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val grey100: Color,
    val grey200: Color,
    val grey300: Color,
    val grey400: Color,
    val grey500: Color,
    val grey600: Color,
    val grey700: Color,
    val grey800: Color,
    val grey900: Color,
    val salaryGreen: Color,
    val accentOrange: Color,
    val success: Color,
    val error: Color,
    val secondary: Color,
    val accent: Color,
) {
    companion object {
        val light = HiredInAdaptivePalette(
            background = HiredInColors.Background,
            surface = HiredInColors.Surface,
            surfaceVariant = HiredInColors.SurfaceVariant,
            grey100 = HiredInColors.Grey100,
            grey200 = HiredInColors.Grey200,
            grey300 = HiredInColors.Grey300,
            grey400 = HiredInColors.Grey400,
            grey500 = HiredInColors.Grey500,
            grey600 = HiredInColors.Grey600,
            grey700 = HiredInColors.Grey700,
            grey800 = HiredInColors.Grey800,
            grey900 = HiredInColors.Grey900,
            salaryGreen = HiredInColors.SalaryGreen,
            accentOrange = HiredInColors.AccentOrange,
            success = HiredInColors.Success,
            error = HiredInColors.Error,
            secondary = HiredInColors.Secondary,
            accent = HiredInColors.Accent,
        )

        val dark = HiredInAdaptivePalette(
            background = HiredInDarkColors.Background,
            surface = HiredInDarkColors.Surface,
            surfaceVariant = HiredInDarkColors.SurfaceVariant,
            grey100 = HiredInDarkColors.Grey100,
            grey200 = HiredInDarkColors.Grey200,
            grey300 = HiredInDarkColors.Grey300,
            grey400 = HiredInDarkColors.Grey400,
            grey500 = HiredInDarkColors.Grey500,
            grey600 = HiredInDarkColors.Grey600,
            grey700 = HiredInDarkColors.Grey700,
            grey800 = HiredInDarkColors.Grey800,
            grey900 = HiredInDarkColors.Grey900,
            salaryGreen = HiredInDarkColors.SalaryGreen,
            accentOrange = HiredInColors.AccentOrange,
            success = HiredInDarkColors.Success,
            error = HiredInColors.Error,
            secondary = HiredInDarkColors.Secondary,
            accent = HiredInDarkColors.Accent,
        )
    }
}

val LocalHiredInAdaptivePalette = staticCompositionLocalOf { HiredInAdaptivePalette.light }

@Composable
fun hiredInColors(): HiredInAdaptivePalette {
    // Prefer a stable CompositionLocal (provided by HiredInTheme) over heuristic checks.
    return LocalHiredInAdaptivePalette.current
}
