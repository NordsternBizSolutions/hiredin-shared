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

object RtlHelper {
    fun isRtl(locale: Locale): Boolean = locale.language == "ar"
    fun layoutDirection(locale: Locale): Int =
        if (isRtl(locale)) android.view.View.LAYOUT_DIRECTION_RTL
        else android.view.View.LAYOUT_DIRECTION_LTR
}
