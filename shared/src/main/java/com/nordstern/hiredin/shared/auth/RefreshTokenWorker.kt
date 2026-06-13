package com.nordstern.hiredin.shared.auth

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RefreshTokenWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val tokenManager: TokenManager
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result =
        if (tokenManager.refreshTokenIfNeeded()) Result.success() else Result.retry()
}
