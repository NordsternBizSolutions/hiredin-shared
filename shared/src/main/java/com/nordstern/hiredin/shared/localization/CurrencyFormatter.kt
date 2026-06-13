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
class CurrencyFormatter @Inject constructor(private val localeManager: LocaleManager) {
    fun format(amount: Number, currencyCode: String = "AED"): String {
        val format = NumberFormat.getCurrencyInstance(localeManager.getCurrentLocale())
        try {
            format.currency = Currency.getInstance(currencyCode)
        } catch (_: Exception) { /* use default */ }
        return format.format(amount)
    }
}
