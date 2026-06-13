package com.nordstern.hiredin.shared.sync.conflict

import com.nordstern.hiredin.shared.database.SyncableEntity
import com.nordstern.hiredin.shared.utils.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MergeStrategy @Inject constructor() : ConflictHandler {
    override fun <T : SyncableEntity> resolve(local: T?, server: T): T {
        // Default merge delegates to server; apps can override with entity-specific logic
        return server
    }
}
