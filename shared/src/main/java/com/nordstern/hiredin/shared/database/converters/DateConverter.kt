package com.nordstern.hiredin.shared.database.converters

import androidx.room.TypeConverter
import com.nordstern.hiredin.shared.sync.ActionStatus
import java.util.Date

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? = date?.time
}
