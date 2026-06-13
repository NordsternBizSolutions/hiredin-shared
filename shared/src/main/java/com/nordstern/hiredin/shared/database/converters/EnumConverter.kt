package com.nordstern.hiredin.shared.database.converters

import androidx.room.TypeConverter
import com.nordstern.hiredin.shared.sync.ActionStatus
import java.util.Date

class EnumConverter {
    @TypeConverter
    fun fromActionStatus(status: ActionStatus): String = status.name

    @TypeConverter
    fun toActionStatus(value: String): ActionStatus = ActionStatus.valueOf(value)
}
