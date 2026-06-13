package com.nordstern.hiredin.shared.database

interface SyncableEntity {
    val id: String
    val updatedAt: Long
    val isDeleted: Boolean get() = false
}

interface BaseDao<T : SyncableEntity> {
    suspend fun getAll(): List<T>
    suspend fun getById(id: String): T?
    suspend fun insertAll(entities: List<T>)
    suspend fun deleteById(id: String)
    suspend fun getUpdatedSince(timestamp: Long): List<T>
}
