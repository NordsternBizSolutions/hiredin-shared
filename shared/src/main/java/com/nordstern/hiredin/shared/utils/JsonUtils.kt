package com.nordstern.hiredin.shared.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonUtils @Inject constructor(private val gson: Gson) {
    fun <T> toJson(obj: T): String = gson.toJson(obj)

    fun <T> fromJson(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)

    inline fun <reified T> fromJson(json: String): T = fromJson(json, T::class.java)

    fun <T> fromJsonList(json: String, clazz: Class<T>): List<T> {
        val type = TypeToken.getParameterized(List::class.java, clazz).type
        @Suppress("UNCHECKED_CAST")
        return gson.fromJson(json, type) as List<T>
    }

    inline fun <reified T> fromJsonList(json: String): List<T> = fromJsonList(json, T::class.java)
}
