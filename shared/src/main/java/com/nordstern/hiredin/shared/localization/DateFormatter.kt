package com.nordstern.hiredin.shared.localization

import android.content.Context
import android.content.res.Configuration
import androidx.core.os.LocaleListCompat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateFormatter @Inject constructor(private val localeManager: LocaleManager) {
    fun format(date: java.util.Date, pattern: String = "dd MMM yyyy"): String =
        SimpleDateFormat(pattern, localeManager.getCurrentLocale()).format(date)

    fun formatDateTime(date: java.util.Date): String =
        SimpleDateFormat("dd MMM yyyy HH:mm", localeManager.getCurrentLocale()).format(date)
}
