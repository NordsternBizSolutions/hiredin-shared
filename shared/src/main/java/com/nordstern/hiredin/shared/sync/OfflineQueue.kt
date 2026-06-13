package com.nordstern.hiredin.shared.sync

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.nordstern.hiredin.shared.BuildConfig
import com.nordstern.hiredin.shared.api.BaseApiClient
import com.nordstern.hiredin.shared.network.NetworkManager
import com.nordstern.hiredin.shared.utils.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Entity(tableName = "offline_actions")
data class OfflineActionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val actionType: String,
    val endpoint: String,
    val method: String,
    val payload: String,
    val headers: String?,
    val createdAt: Long = System.currentTimeMillis(),
    var retryCount: Int = 0,
    var status: ActionStatus = ActionStatus.PENDING,
    var lastAttemptAt: Long? = null,
    var errorMessage: String? = null
)

enum class ActionStatus {
    PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED
}

@Dao
interface OfflineActionDao {
    @Query("SELECT * FROM offline_actions WHERE status IN ('PENDING', 'FAILED') ORDER BY createdAt ASC")
    suspend fun getPendingActions(): List<OfflineActionEntity>

    @Query("SELECT * FROM offline_actions WHERE id = :id")
    suspend fun getActionById(id: String): OfflineActionEntity?

    @Insert
    suspend fun insertAction(action: OfflineActionEntity)

    @Update
    suspend fun updateAction(action: OfflineActionEntity)

    @Query("DELETE FROM offline_actions WHERE id = :id")
    suspend fun deleteAction(id: String)

    @Query("DELETE FROM offline_actions WHERE status IN ('COMPLETED', 'CANCELLED') AND createdAt < :cutoffDate")
    suspend fun cleanOldActions(cutoffDate: Long): Int

    @Query("SELECT COUNT(*) FROM offline_actions WHERE status = 'PENDING'")
    suspend fun getPendingCount(): Int
}

@Database(entities = [OfflineActionEntity::class], version = 1, exportSchema = false)
abstract class OfflineQueueDatabase : RoomDatabase() {
    abstract fun offlineActionDao(): OfflineActionDao
}

@Singleton
class OfflineQueue @Inject constructor(
    private val context: Context,
    private val apiClient: BaseApiClient,
    private val networkManager: NetworkManager
) {
    private val logger = Logger.getLogger("OfflineQueue")
    private val mutex = Mutex()
    private val maxRetries = 3
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val database: OfflineQueueDatabase by lazy {
        Room.databaseBuilder(context, OfflineQueueDatabase::class.java, "offline_queue_db").build()
    }

    private val dao: OfflineActionDao get() = database.offlineActionDao()

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
            payload = apiClient.gson.toJson(payload),
            headers = headers?.let { apiClient.gson.toJson(it) }
        )
        dao.insertAction(action)
        logger.info("Action enqueued: $actionType to $endpoint")
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
                val result = processAction(action)
                if (result.success) {
                    dao.deleteAction(action.id)
                    logger.info("Action processed successfully: ${action.id}")
                } else {
                    val updated = action.copy(
                        retryCount = action.retryCount + 1,
                        lastAttemptAt = System.currentTimeMillis(),
                        errorMessage = result.error,
                        status = if (action.retryCount + 1 >= maxRetries) ActionStatus.FAILED else ActionStatus.PENDING
                    )
                    dao.updateAction(updated)
                    logger.warn("Action failed (${updated.retryCount}/$maxRetries): ${action.id}")
                }
                result
            }
        }
    }

    private suspend fun processAction(action: OfflineActionEntity): OfflineActionResult = try {
        val client = apiClient.getAuthenticatedOkHttpClient()
        val body = if (action.method.uppercase() in listOf("POST", "PUT", "PATCH")) {
            action.payload.toRequestBody(jsonMediaType)
        } else {
            null
        }

        val requestBuilder = Request.Builder()
            .url("${BuildConfig.API_BASE_URL}${action.endpoint}")
            .method(action.method.uppercase(), body)

        action.headers?.let {
            val headersMap = apiClient.gson.fromJson(it, Map::class.java) as Map<*, *>
            headersMap.forEach { (key, value) ->
                requestBuilder.addHeader(key.toString(), value.toString())
            }
        }

        val response = client.newCall(requestBuilder.build()).execute()
        if (response.isSuccessful) {
            OfflineActionResult(success = true, actionId = action.id)
        } else {
            OfflineActionResult(success = false, actionId = action.id, error = "HTTP ${response.code}: ${response.message}")
        }
    } catch (e: Exception) {
        OfflineActionResult(success = false, actionId = action.id, error = e.message ?: "Unknown error")
    }

    suspend fun getPendingCount(): Int = mutex.withLock { dao.getPendingCount() }

    suspend fun cancelAction(actionId: String): Boolean = mutex.withLock {
        val action = dao.getActionById(actionId)
        if (action != null && action.status == ActionStatus.PENDING) {
            dao.updateAction(action.copy(status = ActionStatus.CANCELLED))
            logger.info("Action cancelled: $actionId")
            true
        } else {
            false
        }
    }

    suspend fun cleanOldActions(daysToKeep: Int = 7) {
        val cutoffDate = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
        val deletedCount = dao.cleanOldActions(cutoffDate)
        logger.info("Cleaned $deletedCount old actions")
    }
}

data class OfflineActionResult(val success: Boolean, val actionId: String, val error: String? = null)
