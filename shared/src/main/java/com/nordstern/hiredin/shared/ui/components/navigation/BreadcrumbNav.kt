package com.nordstern.hiredin.shared.ui.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BreadcrumbNav(items: List<String>, modifier: Modifier = Modifier) {
    Text(items.joinToString(" > "), modifier = modifier, style = MaterialTheme.typography.labelMedium)
}
