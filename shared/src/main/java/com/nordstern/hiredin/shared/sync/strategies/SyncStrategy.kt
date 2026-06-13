package com.nordstern.hiredin.shared.sync.strategies

import com.nordstern.hiredin.shared.database.BaseDao
import com.nordstern.hiredin.shared.database.SyncableEntity
import com.nordstern.hiredin.shared.sync.conflict.ConflictHandler

interface SyncStrategy {
    suspend fun <T : SyncableEntity> sync(
        dao: BaseDao<T>,
        entityName: String,
        entityClass: Class<T>,
        lastSync: Long,
        conflictHandler: ConflictHandler
    ): SyncStrategyResult
}
