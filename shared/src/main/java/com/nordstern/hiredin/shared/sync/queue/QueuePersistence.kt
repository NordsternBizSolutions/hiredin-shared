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
class QueuePersistence @Inject constructor(
    private val dao: com.nordstern.hiredin.shared.database.dao.OfflineActionDao
) {
    suspend fun getPending() = dao.getPendingActions()
    suspend fun save(action: OfflineActionEntity) = dao.insertAction(action)
    suspend fun update(action: OfflineActionEntity) = dao.updateAction(action)
    suspend fun delete(id: String) = dao.deleteAction(id)
}
