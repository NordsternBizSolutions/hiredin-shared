package com.nordstern.hiredin.shared.cache

import java.util.LinkedHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpiringCache @Inject constructor() {
    private data class Entry(val value: Any, val expiresAt: Long)
    private val cache = mutableMapOf<String, Entry>()

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? {
        val entry = cache[key] ?: return null
        if (System.currentTimeMillis() > entry.expiresAt) {
            cache.remove(key)
            return null
        }
        return entry.value as? T
    }

    fun put(key: String, value: Any, ttlMs: Long) {
        cache[key] = Entry(value, System.currentTimeMillis() + ttlMs)
    }
}
