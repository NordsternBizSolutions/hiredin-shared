package com.nordstern.hiredin.shared.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nordstern.hiredin.shared.database.SyncableEntity
import com.nordstern.hiredin.shared.sync.ActionStatus
import java.util.UUID

@Entity(tableName = "offline_actions")
data class OfflineActionEntity(
    @PrimaryKey override val id: String = UUID.randomUUID().toString(),
    val actionType: String,
    val endpoint: String,
    val method: String,
    val payload: String,
    val headers: String?,
    val createdAt: Long = System.currentTimeMillis(),
    override val updatedAt: Long = System.currentTimeMillis(),
    var retryCount: Int = 0,
    var status: ActionStatus = ActionStatus.PENDING,
    var lastAttemptAt: Long? = null,
    var errorMessage: String? = null
) : SyncableEntity {
    override val isDeleted: Boolean get() = status == ActionStatus.CANCELLED
}

@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey val entityName: String,
    val lastSyncTimestamp: Long,
    override val updatedAt: Long = System.currentTimeMillis()
) : SyncableEntity {
    override val id: String get() = entityName
}
