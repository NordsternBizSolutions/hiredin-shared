package com.nordstern.hiredin.shared.network

import com.nordstern.hiredin.shared.cache.DiskCache
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResponseCacheManager @Inject constructor(
    private val diskCache: DiskCache
) {
    fun get(key: String): String? = diskCache.get(key)
    fun put(key: String, value: String) = diskCache.put(key, value)
    fun invalidate(key: String) = diskCache.remove(key)
    fun clear() = diskCache.clear()
}
