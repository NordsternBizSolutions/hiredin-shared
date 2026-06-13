package com.nordstern.hiredin.shared.api.metrics

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiMetricsCollector @Inject constructor() {
    private val totalRequests = AtomicLong(0)
    private val successfulRequests = AtomicLong(0)
    private val failedRequests = AtomicLong(0)
    private val totalLatencyMs = AtomicLong(0)
    private var lastErrorCode: String? = null

    private val _metrics = MutableStateFlow(ApiMetrics())
    val metrics: StateFlow<ApiMetrics> = _metrics.asStateFlow()

    fun recordSuccess(latencyMs: Long) {
        totalRequests.incrementAndGet()
        successfulRequests.incrementAndGet()
        totalLatencyMs.addAndGet(latencyMs)
        publish()
    }

    fun recordFailure(errorCode: String, latencyMs: Long) {
        totalRequests.incrementAndGet()
        failedRequests.incrementAndGet()
        totalLatencyMs.addAndGet(latencyMs)
        lastErrorCode = errorCode
        publish()
    }

    private fun publish() {
        val total = totalRequests.get()
        val avg = if (total > 0) totalLatencyMs.get() / total else 0L
        _metrics.value = ApiMetrics(
            totalRequests = total,
            successfulRequests = successfulRequests.get(),
            failedRequests = failedRequests.get(),
            averageLatencyMs = avg,
            lastErrorCode = lastErrorCode
        )
    }

    fun reset() {
        totalRequests.set(0)
        successfulRequests.set(0)
        failedRequests.set(0)
        totalLatencyMs.set(0)
        lastErrorCode = null
        publish()
    }
}
