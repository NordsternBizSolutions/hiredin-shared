package com.nordstern.hiredin.shared.analytics.events

data class AnalyticsEvent(
    val name: String,
    val properties: Map<String, Any> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)
