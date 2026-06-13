package com.nordstern.hiredin.shared.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nordstern.hiredin.shared.utils.Logger

class DatabaseCallback : RoomDatabase.Callback() {
    private val logger = Logger.getLogger("DatabaseCallback")

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        logger.info("HiredIn database created")
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        logger.debug("HiredIn database opened")
    }
}
