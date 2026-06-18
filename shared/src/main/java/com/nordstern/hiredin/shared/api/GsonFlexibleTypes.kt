package com.nordstern.hiredin.shared.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.nordstern.hiredin.shared.api.services.InterviewDto
import com.nordstern.hiredin.shared.api.UpcomingInterviewDto
import java.lang.reflect.Type

class FlexibleIdDeserializer : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): String {
        if (json.isJsonNull) return ""
        if (!json.isJsonPrimitive) throw JsonParseException("Expected primitive for id")
        val primitive = json.asJsonPrimitive
        return when {
            primitive.isString -> primitive.asString
            primitive.isNumber -> primitive.asNumber.toString()
            else -> primitive.asString
        }
    }
}

class FlexibleInterviewListDeserializer : JsonDeserializer<List<InterviewDto>> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): List<InterviewDto> = parseInterviewArray(json, context, InterviewDto::class.java)
}

class FlexibleUpcomingInterviewListDeserializer : JsonDeserializer<List<UpcomingInterviewDto>> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): List<UpcomingInterviewDto> = parseInterviewArray(json, context, UpcomingInterviewDto::class.java)
}

private fun <T> parseInterviewArray(
    json: JsonElement,
    context: JsonDeserializationContext,
    clazz: Class<T>
): List<T> {
    val array = when {
        json.isJsonArray -> json.asJsonArray
        json.isJsonObject -> {
            val obj = json.asJsonObject
            obj.get("interviews")
                ?: obj.get("items")
                ?: obj.get("results")
                ?: obj.get("upcoming")
                ?: obj.get("scheduled")
                ?: obj.get("invites")
                ?: obj.get("data")
                ?: return emptyList()
        }
        else -> return emptyList()
    }
    if (!array.isJsonArray) return emptyList()
    val results = mutableListOf<T>()
    array.asJsonArray.forEach { element ->
        try {
            results.add(context.deserialize(element, clazz))
        } catch (_: Exception) {
        }
    }
    return results
}
