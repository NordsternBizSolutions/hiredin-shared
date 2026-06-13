package com.nordstern.hiredin.shared.workers.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.nordstern.hiredin.shared.sync.OfflineQueue
import com.nordstern.hiredin.shared.sync.SyncScheduler
import com.nordstern.hiredin.shared.workers.BaseWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val offlineQueue: OfflineQueue,
    private val syncScheduler: SyncScheduler
) : BaseWorker(context, params) {
    override suspend fun executeWork(): Result {
        offlineQueue.processQueue()
        syncScheduler.scheduleImmediateSync()
        return Result.success()
    }
}
