package com.nordstern.hiredin.shared.logging

import com.nordstern.hiredin.shared.logging.formatters.PrettyLogFormatter
import com.nordstern.hiredin.shared.logging.loggers.ConsoleLogger
import com.nordstern.hiredin.shared.logging.loggers.FileLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogManager @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) {
    private val formatter: LogFormatter = PrettyLogFormatter()
    private val sinks = mutableListOf<LogSink>(
        ConsoleLogger(formatter),
        FileLogger(context, formatter)
    )

    fun addSink(sink: LogSink) {
        sinks.add(sink)
    }

    fun log(level: LogLevel, tag: String, message: String, throwable: Throwable? = null) {
        val entry = LogEntry(level, tag, message, throwable = throwable)
        sinks.filter { it.isEnabled(level) }.forEach { it.write(entry) }
    }

    fun debug(tag: String, message: String) = log(LogLevel.DEBUG, tag, message)
    fun info(tag: String, message: String) = log(LogLevel.INFO, tag, message)
    fun warn(tag: String, message: String, throwable: Throwable? = null) = log(LogLevel.WARN, tag, message, throwable)
    fun error(tag: String, message: String, throwable: Throwable? = null) = log(LogLevel.ERROR, tag, message, throwable)
}
