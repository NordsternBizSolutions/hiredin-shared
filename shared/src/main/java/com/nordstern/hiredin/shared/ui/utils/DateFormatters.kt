package com.nordstern.hiredin.shared.ui.utils

import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatters {
    private val isoFormatter = DateTimeFormatter.ISO_INSTANT
    private val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.getDefault())

    fun formatIso(timestamp: Long): String = isoFormatter.format(Instant.ofEpochMilli(timestamp))
    fun formatDisplay(timestamp: Long): String = displayFormatter.format(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()))
    fun formatDateTime(timestamp: Long): String = dateTimeFormatter.format(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()))
}
