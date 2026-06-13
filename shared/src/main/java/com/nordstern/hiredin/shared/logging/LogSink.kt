package com.nordstern.hiredin.shared.logging

interface LogSink {
    fun write(entry: LogEntry)
    fun isEnabled(level: LogLevel): Boolean = true
}
