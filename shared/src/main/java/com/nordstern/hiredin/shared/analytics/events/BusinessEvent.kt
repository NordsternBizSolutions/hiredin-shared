package com.nordstern.hiredin.shared.analytics.events

data class BusinessEvent(
    val name: String,
    val properties: Map<String, Any> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toAnalyticsEvent(): AnalyticsEvent = AnalyticsEvent(name, properties, timestamp)
}
