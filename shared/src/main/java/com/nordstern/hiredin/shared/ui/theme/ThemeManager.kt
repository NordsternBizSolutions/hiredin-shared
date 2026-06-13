package com.nordstern.hiredin.shared.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

object ThemeManager {
    var forceDarkTheme: Boolean? = null

    @Composable
    fun shouldUseDarkTheme(systemDark: Boolean = androidx.compose.foundation.isSystemInDarkTheme()): Boolean =
        forceDarkTheme ?: systemDark
}
