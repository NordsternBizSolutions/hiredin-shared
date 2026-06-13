package com.nordstern.hiredin.shared.workers.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.nordstern.hiredin.shared.workers.BaseWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LogCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : BaseWorker(context, params) {
    override suspend fun executeWork(): Result = Result.success()
}
