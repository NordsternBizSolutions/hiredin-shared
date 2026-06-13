package com.nordstern.hiredin.shared.api

import com.nordstern.hiredin.shared.BuildConfig

/**
 * Allows consuming apps to override the API base URL at runtime
 * (required when using a prebuilt JitPack artifact).
 */
object ApiConfig {
    private var overrideBaseUrl: String? = null

    fun setBaseUrl(url: String) {
        overrideBaseUrl = url.trim().let { if (it.endsWith("/")) it else "$it/" }
    }

    fun getBaseUrl(): String = overrideBaseUrl ?: BuildConfig.API_BASE_URL
}
