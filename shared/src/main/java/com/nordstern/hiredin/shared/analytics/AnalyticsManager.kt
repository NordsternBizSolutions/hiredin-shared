package com.nordstern.hiredin.shared.analytics

import com.nordstern.hiredin.shared.analytics.events.AnalyticsEvent
import com.nordstern.hiredin.shared.analytics.providers.AnalyticsProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards AnalyticsProvider>
) {
    fun track(name: String, properties: Map<String, Any> = emptyMap()) {
        val event = AnalyticsEvent(name, properties)
        providers.forEach { it.trackEvent(event) }
    }

    fun setUserProperty(key: String, value: String) {
        providers.forEach { it.setUserProperty(key, value) }
    }

    fun setUserId(userId: String?) {
        providers.forEach { it.setUserId(userId) }
    }
}
