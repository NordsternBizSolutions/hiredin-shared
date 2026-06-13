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
class IncrementalSyncStrategy @Inject constructor(
    private val apiClient: BaseApiClient,
    private val gson: Gson,
    private val conflictHandler: ConflictHandler
) : SyncStrategy {

    override suspend fun <T : SyncableEntity> sync(
        dao: BaseDao<T>,
        entityName: String,
        entityClass: Class<T>,
        lastSync: Long,
        conflictHandler: ConflictHandler
    ): SyncStrategyResult {
        val response = apiClient.safeApiCall {
            apiClient.createAuthenticatedService<SyncApi>().getChanges(
                entities = entityName,
                lastSync = lastSync
            )
        }

        if (!response.success || response.data == null) {
            throw IllegalStateException(response.error ?: "Sync failed for $entityName")
        }

        return applyChanges(dao, entityName, entityClass, response.data, conflictHandler)
    }

    private suspend fun <T : SyncableEntity> applyChanges(
        dao: BaseDao<T>,
        entityName: String,
        entityClass: Class<T>,
        data: SyncChangesResponse,
        conflictHandler: ConflictHandler
    ): SyncStrategyResult {
        val serverElements: List<JsonElement> = data.entities[entityName].orEmpty()
        val deletedIds = data.deletedIds[entityName].orEmpty()

        val resolved = serverElements.mapNotNull { element ->
            try {
                val server = gson.fromJson(element, entityClass)
                conflictHandler.resolve(dao.getById(server.id), server)
            } catch (_: Exception) {
                null
            }
        }

        dao.insertAll(resolved)
        deletedIds.forEach { dao.deleteById(it) }

        return SyncStrategyResult(
            syncedCount = resolved.size,
            deletedCount = deletedIds.size,
            timestamp = data.timestamp ?: System.currentTimeMillis()
        )
    }
}
