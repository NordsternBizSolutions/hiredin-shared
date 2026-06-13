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

class ConsoleLogger(private val formatter: LogFormatter) : LogSink {
    override fun write(entry: LogEntry) {
        val message = formatter.format(entry)
        when (entry.level) {
            LogLevel.DEBUG -> Log.d(entry.tag, message)
            LogLevel.INFO -> Log.i(entry.tag, message)
            LogLevel.WARN -> Log.w(entry.tag, message, entry.throwable)
            LogLevel.ERROR -> Log.e(entry.tag, message, entry.throwable)
        }
    }
}
