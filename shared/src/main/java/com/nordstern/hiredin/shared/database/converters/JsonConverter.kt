package com.nordstern.hiredin.shared.database.converters

import androidx.room.TypeConverter
import com.nordstern.hiredin.shared.sync.ActionStatus
import java.util.Date

class JsonConverter {
    @TypeConverter
    fun fromString(value: String?): Map<String, String>? =
        value?.split("|")?.mapNotNull {
            val parts = it.split("=", limit = 2)
            if (parts.size == 2) parts[0] to parts[1] else null
        }?.toMap()

    @TypeConverter
    fun toString(map: Map<String, String>?): String? =
        map?.entries?.joinToString("|") { "${it.key}=${it.value}" }
}
