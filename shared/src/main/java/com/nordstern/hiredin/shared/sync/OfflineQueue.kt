package com.nordstern.hiredin.shared.sync

import com.google.gson.Gson
import com.nordstern.hiredin.shared.api.BaseApiClient
import com.nordstern.hiredin.shared.database.dao.OfflineActionDao
import com.nordstern.hiredin.shared.database.entities.OfflineActionEntity
import com.nordstern.hiredin.shared.network.NetworkManager
import com.nordstern.hiredin.shared.sync.queue.ActionProcessor
import com.nordstern.hiredin.shared.sync.queue.RetryPolicy
import com.nordstern.hiredin.shared.utils.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineQueue @Inject constructor(
    private val dao: OfflineActionDao,
    private val gson: Gson,
    private val networkManager: NetworkManager,
    private val actionProcessor: ActionProcessor,
    private val retryPolicy: RetryPolicy
) {
    private val logger = Logger.getLogger("OfflineQueue")
    private val mutex = Mutex()

    suspend fun enqueueAction(
        actionType: String,
        endpoint: String,
        method: String,
        payload: Any,
        headers: Map<String, String>? = null
    ): String = mutex.withLock {
        val action = OfflineActionEntity(
            actionType = actionType,
            endpoint = endpoint,
            method = method,
            payload = gson.toJson(payload),
            headers = headers?.let { gson.toJson(it) }
        )
        dao.insertAction(action)
        logger.info("Action enqueued: $actionType -> $endpoint")
        action.id
    }

    suspend fun getPendingActions(): List<OfflineActionEntity> = mutex.withLock {
        dao.getPendingActions()
    }

    suspend fun processQueue(): List<OfflineActionResult> {
        if (!networkManager.isNetworkAvailable()) {
            logger.info("Network unavailable, skipping queue processing")
            return emptyList()
        }

        return mutex.withLock {
            val actions = dao.getPendingActions()
            if (actions.isEmpty()) return emptyList()

            logger.info("Processing ${actions.size} pending actions")
            actions.map { action ->
                val result = actionProcessor.process(action)
                if (result.success) {
                    dao.deleteAction(action.id)
                    logger.info("Action processed: ${action.id}")
                } else {
                    val updated = action.copy(
                        retryCount = action.retryCount + 1,
                        lastAttemptAt = System.currentTimeMillis(),
                        errorMessage = result.error,
                        status = if (retryPolicy.shouldRetry(action.retryCount + 1)) {
                            ActionStatus.PENDING
                        } else {
                            ActionStatus.FAILED
                        },
                        updatedAt = System.currentTimeMillis()
                    )
                    dao.updateAction(updated)
                }
                result
            }
        }
    }

    suspend fun getPendingCount(): Int = mutex.withLock { dao.getPendingCount() }

    suspend fun cancelAction(actionId: String): Boolean = mutex.withLock {
        val action = dao.getActionById(actionId) ?: return false
        if (action.status != ActionStatus.PENDING) return false
        dao.updateAction(action.copy(status = ActionStatus.CANCELLED, updatedAt = System.currentTimeMillis()))
        logger.info("Action cancelled: $actionId")
        true
    }

    suspend fun cleanOldActions(daysToKeep: Int = 7) {
        val cutoff = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
        val deleted = dao.cleanOldActions(cutoff)
        logger.info("Cleaned $deleted old actions")
    }
}

data class OfflineActionResult(val success: Boolean, val actionId: String, val error: String? = null)
