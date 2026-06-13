package com.nordstern.hiredin.shared.sync.strategies

data class SyncStrategyResult(
    val syncedCount: Int,
    val deletedCount: Int,
    val timestamp: Long
)
