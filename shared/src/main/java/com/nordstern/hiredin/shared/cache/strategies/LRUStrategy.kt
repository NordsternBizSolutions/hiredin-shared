package com.nordstern.hiredin.shared.cache.strategies

import com.nordstern.hiredin.shared.cache.CacheStrategy

class LRUStrategy(private val maxSize: Int, private val accessOrder: MutableList<String>) : CacheStrategy {
    override fun shouldEvict(key: String): Boolean = accessOrder.size > maxSize && accessOrder.firstOrNull() == key
}
