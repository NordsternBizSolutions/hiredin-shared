package com.nordstern.hiredin.shared.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nordstern.hiredin.shared.database.converters.DateConverter
import com.nordstern.hiredin.shared.database.converters.EnumConverter
import com.nordstern.hiredin.shared.database.converters.JsonConverter
import com.nordstern.hiredin.shared.database.dao.OfflineActionDao
import com.nordstern.hiredin.shared.database.dao.SyncMetadataDao
import com.nordstern.hiredin.shared.database.entities.OfflineActionEntity
import com.nordstern.hiredin.shared.database.entities.SyncMetadataEntity

@Database(
    entities = [OfflineActionEntity::class, SyncMetadataEntity::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(DateConverter::class, EnumConverter::class, JsonConverter::class)
abstract class HiredInDatabase : RoomDatabase() {
    abstract fun offlineActionDao(): OfflineActionDao
    abstract fun syncMetadataDao(): SyncMetadataDao
}
