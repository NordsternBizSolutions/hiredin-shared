package com.nordstern.hiredin.shared.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.nordstern.hiredin.shared.workers.workers.CacheCleanupWorker
import com.nordstern.hiredin.shared.workers.workers.TokenRefreshWorker
import com.nordstern.hiredin.shared.utils.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerScheduler @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun scheduleTokenRefresh() {
        val request = PeriodicWorkRequestBuilder<TokenRefreshWorker>(6, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        workManager.enqueueUniquePeriodicWork("token_refresh", ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun scheduleCacheCleanup() {
        val request = PeriodicWorkRequestBuilder<CacheCleanupWorker>(24, TimeUnit.HOURS).build()
        workManager.enqueueUniquePeriodicWork("cache_cleanup", ExistingPeriodicWorkPolicy.KEEP, request)
    }
}
