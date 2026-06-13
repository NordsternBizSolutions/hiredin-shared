package com.nordstern.hiredin.shared.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ResponseParser {
    private val gson = Gson()

    fun <T> parse(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)

    fun <T> parseList(json: String, clazz: Class<T>): List<T> {
        val type = TypeToken.getParameterized(List::class.java, clazz).type
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
