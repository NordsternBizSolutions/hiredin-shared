package com.nordstern.hiredin.shared.sync

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.nordstern.hiredin.shared.api.BaseApiClient
import com.nordstern.hiredin.shared.api.SyncApi
import com.nordstern.hiredin.shared.database.BaseDao
import com.nordstern.hiredin.shared.database.SyncableEntity
import com.nordstern.hiredin.shared.network.NetworkManager
import com.nordstern.hiredin.shared.sync.conflict.ConflictHandler
import com.nordstern.hiredin.shared.sync.strategies.SyncStrategy
import com.nordstern.hiredin.shared.utils.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

data class SyncChangesResponse(
    val entities: Map<String, List<JsonElement>> = emptyMap(),
    val deletedIds: Map<String, List<String>> = emptyMap(),
    val timestamp: Long? = null
)

@Singleton
class SyncManager @Inject constructor(
    private val apiClient: BaseApiClient,
    private val gson: Gson,
    private val networkManager: NetworkManager,
    private val offlineQueue: OfflineQueue,
    private val syncStrategy: SyncStrategy,
    private val conflictHandler: ConflictHandler,
    private val syncScheduler: SyncScheduler,
    private val syncStateManager: SyncStateManager
) {
    private val logger = Logger.getLogger("SyncManager")
    private val mutex = Mutex()

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: Flow<SyncState> = _syncState.asStateFlow()
    val lastSyncTimestamp = syncStateManager.lastSyncTimestamps

    sealed class SyncState {
        data object Idle : SyncState()
        data object InProgress : SyncState()
        data class Completed(val timestamp: Long) : SyncState()
        data class Failed(val error: String) : SyncState()
        data object Offline : SyncState()
    }

    suspend fun <T : SyncableEntity> syncEntity(
        dao: BaseDao<T>,
        entityName: String,
        entityClass: Class<T>,
        lastSync: Long? = null
    ): SyncResult {
        if (!networkManager.isNetworkAvailable()) {
            _syncState.value = SyncState.Offline
            return SyncResult.Offline
        }

        return mutex.withLock {
            try {
                _syncState.value = SyncState.InProgress
                val syncTimestamp = lastSync ?: syncStateManager.getTimestamp(entityName)

                val response = apiClient.safeApiCall {
                    apiClient.createAuthenticatedService<SyncApi>().getChanges(
                        entities = entityName,
                        lastSync = syncTimestamp
                    )
                }

                if (response.success && response.data != null) {
                    val serverElements = response.data.entities[entityName].orEmpty()
                    val deletedIds = response.data.deletedIds[entityName].orEmpty()

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

                    val newTimestamp = response.data.timestamp ?: System.currentTimeMillis()
                    syncStateManager.updateTimestamp(entityName, newTimestamp)
                    _syncState.value = SyncState.Completed(newTimestamp)

                    SyncResult.Success(resolved.size, deletedIds.size, newTimestamp)
                } else {
                    val error = response.error ?: "Sync failed"
                    _syncState.value = SyncState.Failed(error)
                    SyncResult.Failure(error)
                }
            } catch (e: Exception) {
                logger.error("Sync error for $entityName", e)
                _syncState.value = SyncState.Failed(e.message ?: "Sync error")
                SyncResult.Failure(e.message ?: "Sync error")
            }
        }
    }

    suspend fun syncAllEntities(
        syncConfig: Map<String, Pair<BaseDao<out SyncableEntity>, Class<out SyncableEntity>>>
    ): Map<String, SyncResult> {
        val results = mutableMapOf<String, SyncResult>()
        for ((entityName, config) in syncConfig) {
            @Suppress("UNCHECKED_CAST")
            val result = syncEntity(
                config.first as BaseDao<SyncableEntity>,
                entityName,
                config.second as Class<SyncableEntity>
            )
            results[entityName] = result
        }
        if (results.values.all { it is SyncResult.Success || it is SyncResult.Offline }) {
            offlineQueue.processQueue()
        }
        return results
    }

    fun schedulePeriodicSync() = syncScheduler.schedulePeriodicSync()
    fun cancelPeriodicSync() = syncScheduler.cancelPeriodicSync()
    fun triggerImmediateSync(entityName: String? = null) = syncScheduler.scheduleImmediateSync(entityName)
}

sealed class SyncResult {
    data class Success(val syncedCount: Int, val deletedCount: Int, val timestamp: Long) : SyncResult()
    data class Failure(val error: String) : SyncResult()
    data object Offline : SyncResult()
}
