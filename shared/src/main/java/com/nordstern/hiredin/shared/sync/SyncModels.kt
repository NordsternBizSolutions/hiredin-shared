package com.nordstern.hiredin.shared.sync

import com.google.gson.JsonElement
import com.nordstern.hiredin.shared.database.SyncableEntity
import com.nordstern.hiredin.shared.utils.Logger
import javax.inject.Inject
import javax.inject.Singleton

data class SyncChangesResponse(
    val entities: Map<String, List<JsonElement>> = emptyMap(),
    val deletedIds: Map<String, List<String>> = emptyMap(),
    val timestamp: Long? = null
)

@Singleton
class ConflictResolver @Inject constructor() {
    private val logger = Logger.getLogger("ConflictResolver")

    fun <T : SyncableEntity> resolveConflicts(
        localEntities: List<T>,
        serverEntities: List<T>
    ): List<T> {
        val localMap = localEntities.associateBy { it.id }
        return serverEntities.map { server ->
            val local = localMap[server.id]
            if (local != null && local.updatedAt > server.updatedAt) {
                logger.debug("Keeping local version for ${server.id}")
                local
            } else {
                server
            }
        }
    }
}
