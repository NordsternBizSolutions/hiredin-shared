package com.nordstern.hiredin.shared.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf

object OneTimeWorker {
    inline fun <reified W : ListenableWorker> enqueue(
        context: Context,
        noinline requestCustomizer: OneTimeWorkRequest.Builder.() -> Unit = {}
    ): OneTimeWorkRequest {
        val request = androidx.work.OneTimeWorkRequestBuilder<W>()
            .apply(requestCustomizer)
            .build()
        WorkManager.getInstance(context).enqueue(request)
        return request
    }
}
