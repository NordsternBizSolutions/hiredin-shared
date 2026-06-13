package com.nordstern.hiredin.shared.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsTracker @Inject constructor(
    private val analyticsManager: com.nordstern.hiredin.shared.analytics.AnalyticsManager
) {
    fun trackEvent(name: String, properties: Map<String, Any> = emptyMap()) =
        analyticsManager.track(name, properties)

    fun trackScreen(screen: String) =
        analyticsManager.track("screen_view", mapOf("screen" to screen))
}
