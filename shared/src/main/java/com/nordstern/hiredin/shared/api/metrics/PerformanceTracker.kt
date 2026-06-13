package com.nordstern.hiredin.shared.api.metrics

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceTracker @Inject constructor(
    private val metricsCollector: ApiMetricsCollector
) {
    fun <T> track(block: () -> T): T {
        val start = System.currentTimeMillis()
        return try {
            val result = block()
            metricsCollector.recordSuccess(System.currentTimeMillis() - start)
            result
        } catch (e: Exception) {
            metricsCollector.recordFailure("exception", System.currentTimeMillis() - start)
            throw e
        }
    }
}
