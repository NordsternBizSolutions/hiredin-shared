package com.nordstern.hiredin.shared.sync.queue

import com.nordstern.hiredin.shared.BuildConfig
import com.nordstern.hiredin.shared.api.BaseApiClient
import com.nordstern.hiredin.shared.database.entities.OfflineActionEntity
import com.nordstern.hiredin.shared.sync.ActionStatus
import com.nordstern.hiredin.shared.sync.OfflineActionResult
import com.nordstern.hiredin.shared.utils.Logger
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetryPolicy @Inject constructor() {
    private val maxRetries = 3
    private val baseDelayMs = 5_000L

    fun shouldRetry(attemptCount: Int): Boolean = attemptCount < maxRetries

    fun getDelayMs(attemptCount: Int): Long = baseDelayMs * attemptCount
}
