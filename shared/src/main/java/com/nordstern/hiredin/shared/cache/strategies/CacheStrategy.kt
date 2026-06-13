package com.nordstern.hiredin.shared.cache

interface CacheStrategy {
    fun shouldEvict(key: String): Boolean
}
