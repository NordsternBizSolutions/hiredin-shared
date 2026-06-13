package com.nordstern.hiredin.shared.logging

data class LogEntry(
    val level: LogLevel,
    val tag: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val throwable: Throwable? = null
)
