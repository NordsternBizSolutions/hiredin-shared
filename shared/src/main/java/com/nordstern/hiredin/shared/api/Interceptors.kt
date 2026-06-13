package com.nordstern.hiredin.shared.api

import com.nordstern.hiredin.shared.auth.TokenManager
import com.nordstern.hiredin.shared.build.constants.SharedConstants
import com.nordstern.hiredin.shared.utils.Logger
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = runBlocking { tokenManager.getAccessToken() }
        val deviceId = runBlocking { tokenManager.getDeviceId() }

        val builder = original.newBuilder()
        if (token != null) {
            builder.header(ApiConstants.HEADER_AUTHORIZATION, "${ApiConstants.BEARER_PREFIX}$token")
        }
        if (deviceId != null) {
            builder.header(ApiConstants.HEADER_DEVICE_ID, deviceId)
        }
        builder.header("X-Client-Type", SharedConstants.DEVICE_TYPE_ANDROID)
        return chain.proceed(builder.build())
    }
}

class RetryInterceptor(private val maxRetries: Int = ApiConstants.DEFAULT_RETRY_COUNT) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var response: Response? = null
        var exception: IOException? = null

        while (attempt < maxRetries) {
            try {
                response?.close()
                response = chain.proceed(chain.request())
                if (response.isSuccessful || response.code in 400..499) return response
            } catch (e: IOException) {
                exception = e
            }
            attempt++
        }

        response?.let { return it }
        throw exception ?: IOException("Max retries exceeded")
    }
}

class LoggingInterceptor : Interceptor {
    private val logger = Logger.getLogger("HTTP")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val start = System.currentTimeMillis()
        val response = chain.proceed(request)
        logger.debug("${request.method} ${request.url} -> ${response.code} (${System.currentTimeMillis() - start}ms)")
        return response
    }
}
