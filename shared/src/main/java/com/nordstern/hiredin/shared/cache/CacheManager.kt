package com.nordstern.hiredin.shared.cache

object CacheManager {
    fun createKey(vararg parts: String): String = parts.joinToString(":")
}
