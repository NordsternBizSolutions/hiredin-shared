package com.nordstern.hiredin.shared.ui.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BottomNavigationBar(items: List<NavItem>, selectedRoute: String, onItemSelected: (NavItem) -> Unit, modifier: Modifier = Modifier) {
    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.route == selectedRoute,
                onClick = { onItemSelected(item) },
                icon = { Icon(Icons.Default.Menu, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
