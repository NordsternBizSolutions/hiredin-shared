package com.nordstern.hiredin.shared.logging

interface LogFormatter {
    fun format(entry: LogEntry): String
}
