package com.nordstern.hiredin.shared.workers.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.nordstern.hiredin.shared.auth.TokenManager
import com.nordstern.hiredin.shared.workers.BaseWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TokenRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val tokenManager: TokenManager
) : BaseWorker(context, params) {
    override suspend fun executeWork(): Result =
        if (tokenManager.refreshTokenIfNeeded()) Result.success() else Result.retry()
}
