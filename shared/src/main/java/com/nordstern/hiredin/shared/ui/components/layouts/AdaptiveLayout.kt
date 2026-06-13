package com.nordstern.hiredin.shared.ui.components.layouts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdaptiveLayout(modifier: Modifier = Modifier, compact: @Composable () -> Unit, expanded: @Composable () -> Unit) {
    BoxWithConstraints(modifier) {
        if (maxWidth < 600.dp) compact() else expanded()
    }
}
