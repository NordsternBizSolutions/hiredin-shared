package com.nordstern.hiredin.shared.analytics.providers

import com.nordstern.hiredin.shared.analytics.events.AnalyticsEvent
import com.nordstern.hiredin.shared.analytics.providers.AnalyticsProvider
import com.nordstern.hiredin.shared.utils.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomAnalyticsProvider @Inject constructor() : AnalyticsProvider {
    private val logger = Logger.getLogger("Analytics")

    override fun trackEvent(event: AnalyticsEvent) {
        logger.debug("Event: ${event.name} ${event.properties}")
    }

    override fun setUserProperty(key: String, value: String) {
        logger.debug("UserProperty: $key=$value")
    }

    override fun setUserId(userId: String?) {
        logger.debug("UserId: $userId")
    }
}
