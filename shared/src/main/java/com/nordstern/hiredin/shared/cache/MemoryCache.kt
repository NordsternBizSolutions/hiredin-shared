package com.nordstern.hiredin.shared.cache

import java.util.LinkedHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryCache @Inject constructor() {
    private val cache = LinkedHashMap<String, Any>(16, 0.75f, true)
    private val maxSize = 100

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? = cache[key] as? T

    fun put(key: String, value: Any) {
        if (cache.size >= maxSize) {
            cache.entries.firstOrNull()?.let { cache.remove(it.key) }
        }
        cache[key] = value
    }

    fun remove(key: String) { cache.remove(key) }
    fun clear() { cache.clear() }
}
