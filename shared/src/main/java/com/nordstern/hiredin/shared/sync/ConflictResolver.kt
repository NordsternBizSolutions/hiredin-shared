package com.nordstern.hiredin.shared.sync

import com.nordstern.hiredin.shared.database.SyncableEntity
import com.nordstern.hiredin.shared.sync.conflict.ConflictHandler
import com.nordstern.hiredin.shared.sync.conflict.LastWriteWins
import com.nordstern.hiredin.shared.sync.conflict.ManualResolutionHandler
import com.nordstern.hiredin.shared.sync.conflict.MergeStrategy
import javax.inject.Inject
import javax.inject.Singleton

enum class ConflictResolutionMode { LAST_WRITE_WINS, MERGE, MANUAL }

@Singleton
class ConflictResolver @Inject constructor(
    private val lastWriteWins: LastWriteWins,
    private val mergeStrategy: MergeStrategy,
    private val manualResolutionHandler: ManualResolutionHandler
) {
    fun handlerFor(mode: ConflictResolutionMode): ConflictHandler = when (mode) {
        ConflictResolutionMode.LAST_WRITE_WINS -> lastWriteWins
        ConflictResolutionMode.MERGE -> mergeStrategy
        ConflictResolutionMode.MANUAL -> manualResolutionHandler
    }

    fun <T : SyncableEntity> resolve(
        mode: ConflictResolutionMode,
        local: T?,
        server: T
    ): T = handlerFor(mode).resolve(local, server)
}
