package com.nordstern.hiredin.shared.ui.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NavigationDrawer(isOpen: Boolean, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    ModalNavigationDrawer(drawerState = rememberDrawerState(if (isOpen) DrawerValue.Open else DrawerValue.Closed), drawerContent = { ModalDrawerSheet { content() } }, content = {})
}
