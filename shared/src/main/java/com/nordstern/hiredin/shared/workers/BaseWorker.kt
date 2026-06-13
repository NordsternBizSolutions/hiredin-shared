package com.nordstern.hiredin.shared.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nordstern.hiredin.shared.utils.Logger

abstract class BaseWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    protected val logger = Logger.getLogger(javaClass.simpleName)

    override suspend fun doWork(): Result {
        return try {
            executeWork()
        } catch (e: Exception) {
            logger.error("Worker failed: ${e.message}", e)
            Result.failure()
        }
    }

    protected abstract suspend fun executeWork(): Result
}
