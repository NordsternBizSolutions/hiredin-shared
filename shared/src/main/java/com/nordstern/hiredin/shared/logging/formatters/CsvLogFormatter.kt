package com.nordstern.hiredin.shared.logging.formatters

import com.nordstern.hiredin.shared.logging.LogEntry
import com.nordstern.hiredin.shared.logging.LogFormatter

class CsvLogFormatter : LogFormatter {
    override fun format(entry: LogEntry): String {
        val message = entry.message.replace("\"", "'")
        return "${entry.timestamp},${entry.level.name},${entry.tag},\"$message\""
    }
}
