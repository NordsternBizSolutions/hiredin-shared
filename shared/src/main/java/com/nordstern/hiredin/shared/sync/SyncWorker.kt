package com.nordstern.hiredin.shared.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nordstern.hiredin.shared.utils.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val offlineQueue: OfflineQueue
) : CoroutineWorker(context, params) {

    private val logger = Logger.getLogger("SyncWorker")

    override suspend fun doWork(): Result {
        val entityName = inputData.getString("entity_name") ?: "all"
        logger.info("Starting sync worker for: $entityName")

        return try {
            offlineQueue.processQueue()
            Result.success()
        } catch (e: Exception) {
            logger.error("Sync worker failed", e)
            Result.retry()
        }
    }
}
