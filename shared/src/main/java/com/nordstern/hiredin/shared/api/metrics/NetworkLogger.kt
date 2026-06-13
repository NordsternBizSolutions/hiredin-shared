package com.nordstern.hiredin.shared.api.metrics

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkLogger @Inject constructor() {
    private val logger = com.nordstern.hiredin.shared.utils.Logger.getLogger("NetworkLogger")

    fun logRequest(method: String, url: String) = logger.debug("→ $method $url")
    fun logResponse(method: String, url: String, code: Int, durationMs: Long) =
        logger.debug("← $method $url [$code] ${durationMs}ms")
    fun logError(method: String, url: String, error: Throwable) =
        logger.error("✗ $method $url", error)
}
