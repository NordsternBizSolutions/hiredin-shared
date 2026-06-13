package com.nordstern.hiredin.shared.cache.strategies

import com.nordstern.hiredin.shared.cache.CacheStrategy

class FIFOStrategy(private val maxSize: Int, private val insertionOrder: MutableList<String>) : CacheStrategy {
    override fun shouldEvict(key: String): Boolean =
        insertionOrder.size > maxSize && insertionOrder.firstOrNull() == key
}
