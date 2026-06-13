package com.nordstern.hiredin.shared.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.nordstern.hiredin.shared.api.BaseApiClient
import com.nordstern.hiredin.shared.api.SyncApi
import com.nordstern.hiredin.shared.build.constants.TimeConstants
import com.nordstern.hiredin.shared.database.BaseDao
import com.nordstern.hiredin.shared.database.SyncableEntity
import com.nordstern.hiredin.shared.network.NetworkManager
import com.nordstern.hiredin.shared.utils.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val context: Context,
    private val apiClient: BaseApiClient,
    private val networkManager: NetworkManager,
    private val offlineQueue: OfflineQueue,
    private val conflictResolver: ConflictResolver
) {
    private val logger = Logger.getLogger("SyncManager")
    private val mutex = Mutex()

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: Flow<SyncState> = _syncState.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow<Map<String, Long>>(emptyMap())
    val lastSyncTimestamp: Flow<Map<String, Long>> = _lastSyncTimestamp.asStateFlow()

    companion object {
        private const val SYNC_WORK_NAME = "data_sync_work"
    }

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
            logger.warn("Network unavailable, cannot sync $entityName")
            _syncState.value = SyncState.Offline
            return SyncResult.Offline
        }

        return mutex.withLock {
            try {
                _syncState.value = SyncState.InProgress
                logger.info("Starting sync for $entityName")

                val syncTimestamp = lastSync ?: _lastSyncTimestamp.value[entityName] ?: 0L

                val response = apiClient.safeApiCall {
                    apiClient.createAuthenticatedService<SyncApi>().getChanges(
                        entities = entityName,
                        lastSync = syncTimestamp
                    )
                }

                if (response.success && response.data != null) {
                    val serverEntities = response.data.entities[entityName].orEmpty()
                    val deletedIds = response.data.deletedIds[entityName].orEmpty()

                    val typedServerEntities = serverEntities.mapNotNull { element ->
                        try {
                            apiClient.gson.fromJson(element, entityClass)
                        } catch (_: Exception) {
                            null
                        }
                    }

                    val resolvedEntities = conflictResolver.resolveConflicts(
                        localEntities = dao.getAll(),
                        serverEntities = typedServerEntities
                    )

                    dao.insertAll(resolvedEntities)
                    deletedIds.forEach { dao.deleteById(it) }

                    val newTimestamp = response.data.timestamp ?: System.currentTimeMillis()
                    updateLastSyncTimestamp(entityName, newTimestamp)

                    logger.info("Sync completed for $entityName: ${resolvedEntities.size} entities synced")
                    _syncState.value = SyncState.Completed(newTimestamp)

                    SyncResult.Success(
                        syncedCount = resolvedEntities.size,
                        deletedCount = deletedIds.size,
                        timestamp = newTimestamp
                    )
                } else {
                    logger.error("Sync failed for $entityName: ${response.error}")
                    _syncState.value = SyncState.Failed(response.error ?: "Unknown error")
                    SyncResult.Failure(response.error ?: "Sync failed")
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
            val result = syncEntityTyped(entityName, config.first, config.second)
            results[entityName] = result
            if (result is SyncResult.Failure) {
                logger.warn("Sync failed for $entityName, continuing with other entities")
            }
        }

        if (results.values.all { it is SyncResult.Success || it is SyncResult.Offline }) {
            processOfflineQueue()
        }
        return results
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T : SyncableEntity> syncEntityTyped(
        entityName: String,
        dao: BaseDao<out SyncableEntity>,
        entityClass: Class<out SyncableEntity>
    ): SyncResult = syncEntity(dao as BaseDao<T>, entityName, entityClass as Class<T>)

    private suspend fun processOfflineQueue() {
        val pendingActions = offlineQueue.getPendingActions()
        if (pendingActions.isNotEmpty()) {
            logger.info("Processing ${pendingActions.size} offline actions")
            offlineQueue.processQueue()
        }
    }

    private fun updateLastSyncTimestamp(entityName: String, timestamp: Long) {
        val current = _lastSyncTimestamp.value.toMutableMap()
        current[entityName] = timestamp
        _lastSyncTimestamp.value = current
    }

    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            TimeConstants.SYNC_INTERVAL_HOURS, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
        logger.info("Periodic sync scheduled every ${TimeConstants.SYNC_INTERVAL_HOURS} hours")
    }

    fun cancelPeriodicSync() {
        WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
        logger.info("Periodic sync cancelled")
    }

    fun triggerImmediateSync(entityName: String? = null) {
        scheduleOneTimeSync(entityName ?: "all")
    }

    private fun scheduleOneTimeSync(entityName: String) {
        val data = workDataOf("entity_name" to entityName)
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setInputData(data)
            .build()
        WorkManager.getInstance(context).enqueue(syncRequest)
    }
}

sealed class SyncResult {
    data class Success(val syncedCount: Int, val deletedCount: Int, val timestamp: Long) : SyncResult()
    data class Failure(val error: String) : SyncResult()
    data object Offline : SyncResult()
}
