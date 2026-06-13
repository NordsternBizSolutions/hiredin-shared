package com.nordstern.hiredin.shared.database

interface SyncableEntity {
    val id: String
    val updatedAt: Long
    val isDeleted: Boolean get() = false
}
