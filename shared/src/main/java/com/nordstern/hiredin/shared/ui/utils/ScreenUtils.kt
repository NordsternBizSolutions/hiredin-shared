package com.nordstern.hiredin.shared.ui.utils

import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object ScreenUtils {
    fun isTablet(screenWidthDp: Int): Boolean = screenWidthDp >= 600
    fun columnCount(screenWidthDp: Int): Int = when {
        screenWidthDp >= 840 -> 3
        screenWidthDp >= 600 -> 2
        else -> 1
    }
}
