package com.nordstern.hiredin.shared.cache.serializers

import com.google.gson.Gson

class JsonSerializer<T>(private val clazz: Class<T>, private val gson: Gson = Gson()) {
    fun serialize(value: T): String = gson.toJson(value)
    fun deserialize(json: String): T = gson.fromJson(json, clazz)
}
