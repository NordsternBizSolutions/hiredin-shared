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

class CrashlyticsLogger(private val formatter: LogFormatter) : LogSink {
    override fun isEnabled(level: LogLevel): Boolean = level == LogLevel.ERROR

    override fun write(entry: LogEntry) {
        Log.e("Crashlytics", formatter.format(entry), entry.throwable)
    }
}
