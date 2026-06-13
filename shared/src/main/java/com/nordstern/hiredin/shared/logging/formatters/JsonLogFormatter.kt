package com.nordstern.hiredin.shared.logging.formatters

import com.nordstern.hiredin.shared.logging.LogEntry
import com.nordstern.hiredin.shared.logging.LogFormatter

class JsonLogFormatter : LogFormatter {
    override fun format(entry: LogEntry): String = buildString {
        append("{\"timestamp\":${entry.timestamp}")
        append(",\"level\":\"${entry.level.name}\"")
        append(",\"tag\":\"${entry.tag}\"")
        append(",\"message\":\"${entry.message.replace("\"", "\\\"")}\"")
        if (entry.throwable != null) {
            append(",\"error\":\"${entry.throwable.message?.replace("\"", "\\\"")}\"")
        }
        append("}")
    }
}
