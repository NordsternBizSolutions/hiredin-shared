package com.nordstern.hiredin.shared.ui.utils

import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object NumberFormatters {
    fun formatCurrency(amount: Number, locale: Locale = Locale.getDefault(), currency: String = "AED"): String {
        val format = NumberFormat.getCurrencyInstance(locale)
        return format.format(amount)
    }
    fun formatPercent(value: Float): String = "${(value * 100).toInt()}%"
    fun formatCompact(value: Long): String = when {
        value >= 1_000_000 -> "${value / 1_000_000}M"
        value >= 1_000 -> "${value / 1_000}K"
        else -> value.toString()
    }
}
