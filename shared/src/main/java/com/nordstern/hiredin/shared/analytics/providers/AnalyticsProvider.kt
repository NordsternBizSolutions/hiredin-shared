package com.nordstern.hiredin.shared.analytics.providers

import com.nordstern.hiredin.shared.analytics.events.AnalyticsEvent

interface AnalyticsProvider {
    fun trackEvent(event: AnalyticsEvent)
    fun setUserProperty(key: String, value: String)
    fun setUserId(userId: String?)
}
