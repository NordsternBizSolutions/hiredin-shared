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

class RemoteLogger(
    private val endpoint: String,
    private val formatter: LogFormatter
) : LogSink {
    override fun isEnabled(level: LogLevel): Boolean = level.priority >= LogLevel.WARN.priority

    override fun write(entry: LogEntry) {
        // Hook for remote log shipping — apps inject their endpoint
        Log.w("RemoteLogger", "Would ship to $endpoint: ${formatter.format(entry)}")
    }
}
