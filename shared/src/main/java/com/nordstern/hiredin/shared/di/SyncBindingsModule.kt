package com.nordstern.hiredin.shared.di

import com.nordstern.hiredin.shared.api.BaseApiClient
import com.nordstern.hiredin.shared.sync.conflict.ConflictHandler
import com.nordstern.hiredin.shared.sync.conflict.LastWriteWins
import com.nordstern.hiredin.shared.sync.strategies.IncrementalSyncStrategy
import com.nordstern.hiredin.shared.sync.strategies.SyncStrategy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncBindingsModule {

    @Binds
    @Singleton
    abstract fun bindSyncStrategy(impl: IncrementalSyncStrategy): SyncStrategy

    @Binds
    @Singleton
    abstract fun bindConflictHandler(impl: LastWriteWins): ConflictHandler
}

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    // BaseApiClient uses @Inject constructor — no manual provides needed
}
