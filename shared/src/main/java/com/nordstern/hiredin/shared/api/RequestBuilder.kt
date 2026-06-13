package com.nordstern.hiredin.shared.api

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object RequestBuilder {
    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun jsonBody(body: Any): RequestBody =
        gson.toJson(body).toRequestBody(jsonMediaType)

    fun jsonBody(json: String): RequestBody =
        json.toRequestBody(jsonMediaType)

    fun queryParams(params: Map<String, String?>): Map<String, String> =
        params.filterValues { it != null }.mapValues { it.value!! }
}
