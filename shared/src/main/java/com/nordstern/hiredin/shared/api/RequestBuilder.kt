package com.nordstern.hiredin.shared.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

object ResponseParser {
    private val gson = Gson()

    fun <T> parse(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)

    inline fun <reified T> parseList(json: String): List<T> {
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type)
    }

    fun parseErrorMessage(json: String?): String? {
        if (json.isNullOrBlank()) return null
        return try {
            val response = gson.fromJson(json, ApiResponse::class.java)
            response.error
        } catch (_: Exception) {
            null
        }
    }
}
