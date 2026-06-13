package com.nordstern.hiredin.shared.sync.conflict

import com.nordstern.hiredin.shared.database.SyncableEntity
import com.nordstern.hiredin.shared.utils.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManualResolutionHandler @Inject constructor() : ConflictHandler {
    private val pendingConflicts = mutableMapOf<String, Pair<SyncableEntity, SyncableEntity>>()

    override fun <T : SyncableEntity> resolve(local: T?, server: T): T {
        if (local != null && local.updatedAt != server.updatedAt) {
            pendingConflicts[server.id] = local to server
        }
        return server
    }

    fun getPendingConflicts(): Map<String, Pair<SyncableEntity, SyncableEntity>> = pendingConflicts.toMap()

    fun clearConflict(id: String) {
        pendingConflicts.remove(id)
    }
}
