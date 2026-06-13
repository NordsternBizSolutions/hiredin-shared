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
class LocalizationHelper @Inject constructor(private val localeManager: LocaleManager) {
    fun isRtl(): Boolean = RtlHelper.isRtl(localeManager.getCurrentLocale())
    fun getLanguageCode(): String = localeManager.getCurrentLocale().language
}
