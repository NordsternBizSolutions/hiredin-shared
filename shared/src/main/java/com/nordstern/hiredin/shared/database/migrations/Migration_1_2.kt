package com.nordstern.hiredin.shared.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration_1_2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS sync_metadata (
                entityName TEXT NOT NULL PRIMARY KEY,
                lastSyncTimestamp INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("ALTER TABLE offline_actions ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
    }
}
