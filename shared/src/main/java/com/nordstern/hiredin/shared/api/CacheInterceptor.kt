package com.nordstern.hiredin.shared.api

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class CacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (request.method == "GET") {
            request = request.newBuilder()
                .cacheControl(
                    CacheControl.Builder()
                        .maxStale(5, TimeUnit.MINUTES)
                        .build()
                )
                .build()
        }

        val response = chain.proceed(request)
        return if (request.cacheControl.noCache) {
            response.newBuilder().header("Cache-Control", "no-cache").build()
        } else {
            response
        }
    }
}
