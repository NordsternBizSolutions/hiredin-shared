package com.nordstern.hiredin.shared.sync.strategies

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.nordstern.hiredin.shared.api.BaseApiClient
import com.nordstern.hiredin.shared.api.SyncApi
import com.nordstern.hiredin.shared.database.BaseDao
import com.nordstern.hiredin.shared.database.SyncableEntity
import com.nordstern.hiredin.shared.sync.SyncChangesResponse
import com.nordstern.hiredin.shared.sync.conflict.ConflictHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartSyncStrategy @Inject constructor(
    private val incrementalSyncStrategy: IncrementalSyncStrategy,
    private val fullSyncStrategy: FullSyncStrategy
) : SyncStrategy {
    override suspend fun <T : SyncableEntity> sync(
        dao: BaseDao<T>,
        entityName: String,
        entityClass: Class<T>,
        lastSync: Long,
        conflictHandler: ConflictHandler
    ): SyncStrategyResult {
        return if (lastSync == 0L) {
            fullSyncStrategy.sync(dao, entityName, entityClass, lastSync, conflictHandler)
        } else {
            incrementalSyncStrategy.sync(dao, entityName, entityClass, lastSync, conflictHandler)
        }
    }
}
