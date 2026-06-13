package com.nordstern.hiredin.shared.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration_2_3 : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_offline_actions_status
            ON offline_actions(status)
            """.trimIndent()
        )
    }
}
