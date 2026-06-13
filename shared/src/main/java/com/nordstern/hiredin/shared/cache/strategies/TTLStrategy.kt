package com.nordstern.hiredin.shared.cache.strategies

import com.nordstern.hiredin.shared.cache.CacheStrategy

class TTLStrategy(private val ttlMs: Long, private val timestamps: MutableMap<String, Long>) : CacheStrategy {
    override fun shouldEvict(key: String): Boolean {
        val ts = timestamps[key] ?: return true
        return System.currentTimeMillis() - ts > ttlMs
    }
}
