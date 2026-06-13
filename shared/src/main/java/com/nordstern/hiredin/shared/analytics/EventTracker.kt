package com.nordstern.hiredin.shared.analytics

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventTracker @Inject constructor(private val analyticsManager: AnalyticsManager) {
    fun trackLogin(method: String) = analyticsManager.track("login", mapOf("method" to method))
    fun trackLogout() = analyticsManager.track("logout")
    fun trackApiError(endpoint: String, code: String) =
        analyticsManager.track("api_error", mapOf("endpoint" to endpoint, "code" to code))
}
