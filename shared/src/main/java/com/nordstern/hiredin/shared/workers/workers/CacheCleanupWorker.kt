package com.nordstern.hiredin.shared.workers.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.nordstern.hiredin.shared.cache.DiskCache
import com.nordstern.hiredin.shared.workers.BaseWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CacheCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val diskCache: DiskCache
) : BaseWorker(context, params) {
    override suspend fun executeWork(): Result {
        diskCache.clear()
        return Result.success()
    }
}
