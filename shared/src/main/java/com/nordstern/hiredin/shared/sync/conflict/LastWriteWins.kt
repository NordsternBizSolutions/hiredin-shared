package com.nordstern.hiredin.shared.sync.conflict

import com.nordstern.hiredin.shared.database.SyncableEntity
import com.nordstern.hiredin.shared.utils.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastWriteWins @Inject constructor() : ConflictHandler {
    private val logger = Logger.getLogger("LastWriteWins")

    override fun <T : SyncableEntity> resolve(local: T?, server: T): T {
        if (local == null) return server
        return if (local.updatedAt > server.updatedAt) {
            logger.debug("Conflict resolved: keeping local ${local.id}")
            local
        } else {
            server
        }
    }
}
