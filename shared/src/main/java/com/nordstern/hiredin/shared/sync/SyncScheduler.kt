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
import com.nordstern.hiredin.shared.build.constants.TimeConstants
import com.nordstern.hiredin.shared.utils.Logger
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncScheduler @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    private val logger = Logger.getLogger("SyncScheduler")
    private val workManager = WorkManager.getInstance(context)

    companion object {
        const val SYNC_WORK_NAME = "hiredin_data_sync"
    }

    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<SyncWorker>(TimeConstants.SYNC_INTERVAL_HOURS, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniquePeriodicWork(SYNC_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        logger.info("Periodic sync scheduled every ${TimeConstants.SYNC_INTERVAL_HOURS}h")
    }

    fun scheduleImmediateSync(entityName: String? = null) {
        val data = workDataOf("entity_name" to (entityName ?: "all"))
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setInputData(data)
            .build()
        workManager.enqueue(request)
    }

    fun cancelPeriodicSync() {
        workManager.cancelUniqueWork(SYNC_WORK_NAME)
        logger.info("Periodic sync cancelled")
    }
}
