package com.nordstern.hiredin.shared.logging.formatters

import com.nordstern.hiredin.shared.logging.LogEntry
import com.nordstern.hiredin.shared.logging.LogFormatter
import com.nordstern.hiredin.shared.logging.LogLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrettyLogFormatter : LogFormatter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    override fun format(entry: LogEntry): String {
        val time = dateFormat.format(Date(entry.timestamp))
        val base = "$time ${entry.level.name.padEnd(5)} [${entry.tag}] ${entry.message}"
        return if (entry.throwable != null) {
            "$base\n${entry.throwable.stackTraceToString()}"
        } else base
    }
}
