package com.nordstern.hiredin.shared.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncStateManager @Inject constructor() {
    private val _lastSyncTimestamps = MutableStateFlow<Map<String, Long>>(emptyMap())
    val lastSyncTimestamps = _lastSyncTimestamps.asStateFlow()

    fun updateTimestamp(entityName: String, timestamp: Long) {
        _lastSyncTimestamps.value = _lastSyncTimestamps.value.toMutableMap().apply {
            put(entityName, timestamp)
        }
    }

    fun getTimestamp(entityName: String): Long = _lastSyncTimestamps.value[entityName] ?: 0L
}
