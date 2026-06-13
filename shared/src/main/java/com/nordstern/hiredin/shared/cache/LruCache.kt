package com.nordstern.hiredin.shared.cache

import java.util.LinkedHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LruCache @Inject constructor() {
    private val cache = object : LinkedHashMap<String, Any>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Any>?): Boolean = size > MAX_SIZE
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? = cache[key] as? T
    fun put(key: String, value: Any) { cache[key] = value }
    fun remove(key: String) { cache.remove(key) }
    fun clear() { cache.clear() }

    companion object { private const val MAX_SIZE = 100 }
}
