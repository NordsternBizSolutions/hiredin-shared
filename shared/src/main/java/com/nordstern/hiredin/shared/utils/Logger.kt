package com.nordstern.hiredin.shared.utils

import android.util.Log
import com.nordstern.hiredin.shared.BuildConfig
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

class Logger private constructor(private val tag: String) {

    companion object {
        private var initialized = false
        private val loggers = ConcurrentHashMap<String, Logger>()

        fun init(debug: Boolean = BuildConfig.DEBUG) {
            if (initialized) return
            if (debug) {
                Timber.plant(Timber.DebugTree())
            } else {
                Timber.plant(ReleaseTree())
            }
            initialized = true
        }

        fun getLogger(tag: String): Logger {
            return loggers.getOrPut(tag) { Logger(tag) }
        }
    }

    fun debug(message: String, throwable: Throwable? = null) {
        if (throwable != null) Timber.tag(tag).d(throwable, message) else Timber.tag(tag).d(message)
    }

    fun info(message: String, throwable: Throwable? = null) {
        if (throwable != null) Timber.tag(tag).i(throwable, message) else Timber.tag(tag).i(message)
    }

    fun warn(message: String, throwable: Throwable? = null) {
        if (throwable != null) Timber.tag(tag).w(throwable, message) else Timber.tag(tag).w(message)
    }

    fun error(message: String, throwable: Throwable? = null) {
        if (throwable != null) Timber.tag(tag).e(throwable, message) else Timber.tag(tag).e(message)
    }

    private class ReleaseTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority >= Log.WARN) {
                Log.println(priority, tag ?: "HiredIn", message)
                t?.let { Log.println(priority, tag ?: "HiredIn", Log.getStackTraceString(it)) }
            }
        }
    }
}
