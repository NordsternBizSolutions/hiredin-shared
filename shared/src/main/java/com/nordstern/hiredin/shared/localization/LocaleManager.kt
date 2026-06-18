package com.nordstern.hiredin.shared.localization

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleManager @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    fun getCurrentLocale(): Locale = context.resources.configuration.locales[0]

    fun setLocale(languageCode: String) {
        persistLanguageSync(context, languageCode)
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun supportedLocales(): List<Locale> = listOf(Locale.ENGLISH, Locale.forLanguageTag("ar"))

    companion object {
        private const val PREFS_NAME = "settings_prefs_sync"
        private const val KEY_LANGUAGE = "language"

        fun persistLanguageSync(context: Context, code: String) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LANGUAGE, code)
                .commit()
        }
    }
}
