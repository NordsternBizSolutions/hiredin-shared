package com.nordstern.hiredin.shared.api

object ApiConstants {
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_DEVICE_ID = "X-Device-Id"
    const val HEADER_CLIENT_TYPE = "X-Client-Type"
    const val HEADER_RATE_LIMIT = "X-RateLimit-Limit"
    const val HEADER_RATE_REMAINING = "X-RateLimit-Remaining"
    const val HEADER_RATE_RESET = "X-RateLimit-Reset"
    const val BEARER_PREFIX = "Bearer "
    const val CONTENT_TYPE_JSON = "application/json"
    const val DEFAULT_RETRY_COUNT = 3
    const val DEFAULT_RETRY_DELAY_MS = 1000L
}
