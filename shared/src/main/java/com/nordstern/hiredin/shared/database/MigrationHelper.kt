package com.nordstern.hiredin.shared.database

import androidx.room.RoomDatabase
import androidx.room.migration.Migration

object MigrationHelper {
    fun registerMigrations(builder: RoomDatabase.Builder<*>) {
        builder.addMigrations(*com.nordstern.hiredin.shared.database.migrations.Migrations.ALL)
    }

    fun allMigrations(): Array<Migration> = com.nordstern.hiredin.shared.database.migrations.Migrations.ALL
}
