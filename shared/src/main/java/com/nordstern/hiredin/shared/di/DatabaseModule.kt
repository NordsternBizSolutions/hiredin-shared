package com.nordstern.hiredin.shared.di

import android.content.Context
import androidx.room.Room
import com.nordstern.hiredin.shared.database.HiredInDatabase
import com.nordstern.hiredin.shared.database.dao.OfflineActionDao
import com.nordstern.hiredin.shared.database.migrations.Migrations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideHiredInDatabase(@ApplicationContext context: Context): HiredInDatabase =
        Room.databaseBuilder(context, HiredInDatabase::class.java, "hiredin_db")
            .addMigrations(*Migrations.ALL)
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()

    @Provides
    fun provideOfflineActionDao(database: HiredInDatabase): OfflineActionDao =
        database.offlineActionDao()
}
