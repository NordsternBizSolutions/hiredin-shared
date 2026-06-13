package com.nordstern.hiredin.shared.api.metrics

data class ApiMetrics(
    val totalRequests: Long = 0,
    val successfulRequests: Long = 0,
    val failedRequests: Long = 0,
    val averageLatencyMs: Long = 0,
    val lastErrorCode: String? = null
)
