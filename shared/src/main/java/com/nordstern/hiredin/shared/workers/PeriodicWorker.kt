package com.nordstern.hiredin.shared.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object PeriodicWorker {
    inline fun <reified W : ListenableWorker> schedule(
        context: Context,
        uniqueName: String,
        intervalHours: Long,
        noinline requestCustomizer: PeriodicWorkRequest.Builder.() -> Unit = {}
    ) {
        val request = androidx.work.PeriodicWorkRequestBuilder<W>(intervalHours, TimeUnit.HOURS)
            .apply(requestCustomizer)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            uniqueName,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
