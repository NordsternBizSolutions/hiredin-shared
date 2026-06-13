package com.nordstern.hiredin.shared.testing

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockResponseInterceptor(
    private val responses: Map<String, Pair<Int, String>>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val match = responses.entries.firstOrNull { request.url.encodedPath.contains(it.key) }
        val (code, body) = match?.value ?: (404 to "{}")
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("Mock")
            .body(body.toResponseBody("application/json".toMediaType()))
            .build()
    }
}
