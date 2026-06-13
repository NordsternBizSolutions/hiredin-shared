package com.nordstern.hiredin.shared.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestQueueManager @Inject constructor() {
    private val queue = java.util.concurrent.ConcurrentLinkedQueue<suspend () -> Unit>()

    suspend fun enqueue(operation: suspend () -> Unit) {
        queue.add(operation)
    }

    suspend fun processAll() {
        while (queue.isNotEmpty()) {
            queue.poll()?.invoke()
        }
    }
}
