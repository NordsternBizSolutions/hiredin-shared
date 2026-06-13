package com.nordstern.hiredin.shared.sync.conflict

import com.nordstern.hiredin.shared.database.SyncableEntity

interface ConflictHandler {
    fun <T : SyncableEntity> resolve(local: T?, server: T): T
}
