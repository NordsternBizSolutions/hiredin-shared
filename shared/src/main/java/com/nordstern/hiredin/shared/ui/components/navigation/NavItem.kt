package com.nordstern.hiredin.shared.ui.components.navigation

data class NavItem(
    val label: String,
    val route: String,
    val badge: Int? = null
)
