package com.nordstern.hiredin.shared.logging.loggers

import android.util.Log
import com.nordstern.hiredin.shared.logging.LogEntry
import com.nordstern.hiredin.shared.logging.LogLevel
import com.nordstern.hiredin.shared.logging.LogSink
import com.nordstern.hiredin.shared.logging.LogFormatter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileLogger(
    context: android.content.Context,
    private val formatter: LogFormatter
) : LogSink {
    private val logFile = File(context.filesDir, "hiredin_logs").apply { mkdirs() }
        .resolve("app_${SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())}.log")

    override fun isEnabled(level: LogLevel): Boolean = level.priority >= LogLevel.INFO.priority

    override fun write(entry: LogEntry) {
        FileWriter(logFile, true).use { writer ->
            writer.appendLine(formatter.format(entry))
        }
    }
}
