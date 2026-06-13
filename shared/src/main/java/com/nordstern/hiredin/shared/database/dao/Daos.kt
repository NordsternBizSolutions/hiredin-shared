package com.nordstern.hiredin.shared.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nordstern.hiredin.shared.database.entities.OfflineActionEntity
import com.nordstern.hiredin.shared.database.entities.SyncMetadataEntity
import com.nordstern.hiredin.shared.sync.ActionStatus

@Dao
interface OfflineActionDao {
    @Query("SELECT * FROM offline_actions WHERE status IN ('PENDING', 'FAILED') ORDER BY createdAt ASC")
    suspend fun getPendingActions(): List<OfflineActionEntity>

    @Query("SELECT * FROM offline_actions WHERE id = :id")
    suspend fun getActionById(id: String): OfflineActionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: OfflineActionEntity)

    @Update
    suspend fun updateAction(action: OfflineActionEntity)

    @Query("DELETE FROM offline_actions WHERE id = :id")
    suspend fun deleteAction(id: String)

    @Query("DELETE FROM offline_actions WHERE status IN ('COMPLETED', 'CANCELLED') AND createdAt < :cutoffDate")
    suspend fun cleanOldActions(cutoffDate: Long): Int

    @Query("SELECT COUNT(*) FROM offline_actions WHERE status = 'PENDING'")
    suspend fun getPendingCount(): Int
}

@Dao
interface SyncMetadataDao {
    @Query("SELECT * FROM sync_metadata WHERE entityName = :entityName")
    suspend fun getByEntity(entityName: String): SyncMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(metadata: SyncMetadataEntity)

    @Query("SELECT * FROM sync_metadata")
    suspend fun getAll(): List<SyncMetadataEntity>
}
