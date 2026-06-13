package com.nordstern.hiredin.shared.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.nordstern.hiredin.shared.ui.theme.HiredInTheme

@Composable
fun HiredInThemeWrapper(content: @Composable () -> Unit) {
    HiredInTheme(content = content)
}
