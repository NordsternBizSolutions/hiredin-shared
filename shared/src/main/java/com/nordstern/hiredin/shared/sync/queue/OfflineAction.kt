package com.nordstern.hiredin.shared.sync.queue

import com.nordstern.hiredin.shared.database.entities.OfflineActionEntity
import com.nordstern.hiredin.shared.sync.ActionStatus
import java.util.UUID

data class OfflineAction(
    val id: String = UUID.randomUUID().toString(),
    val actionType: String = "api_call",
    val endpoint: String,
    val method: String,
    val payload: String = "{}",
    val headers: String? = null,
    val status: ActionStatus = ActionStatus.PENDING,
    val attemptCount: Int = 0
) {
    fun toEntity(): OfflineActionEntity = OfflineActionEntity(
        id = id,
        actionType = actionType,
        endpoint = endpoint,
        method = method,
        payload = payload,
        headers = headers,
        status = status,
        retryCount = attemptCount,
        updatedAt = System.currentTimeMillis()
    )
}
