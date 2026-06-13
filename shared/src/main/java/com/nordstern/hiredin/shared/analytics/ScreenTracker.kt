package com.nordstern.hiredin.shared.analytics

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenTracker @Inject constructor(private val analyticsManager: AnalyticsManager) {
    private var currentScreen: String? = null

    fun trackScreen(screenName: String) {
        currentScreen = screenName
        analyticsManager.track("screen_view", mapOf("screen" to screenName))
    }

    fun getCurrentScreen(): String? = currentScreen
}
