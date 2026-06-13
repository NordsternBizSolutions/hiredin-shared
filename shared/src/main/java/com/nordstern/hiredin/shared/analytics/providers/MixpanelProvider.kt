package com.nordstern.hiredin.shared.analytics.providers

import com.nordstern.hiredin.shared.analytics.events.AnalyticsEvent
import com.nordstern.hiredin.shared.utils.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MixpanelProvider @Inject constructor() : AnalyticsProvider {
    override fun trackEvent(event: AnalyticsEvent) {}
    override fun setUserProperty(key: String, value: String) {}
    override fun setUserId(userId: String?) {}
}
