package com.nordstern.hiredin.shared

import android.content.Context
import com.nordstern.hiredin.shared.build.VersionInfo
import com.nordstern.hiredin.shared.utils.Logger

/**
 * Entry point for initializing the HiredIn Shared Library in consuming apps.
 *
 * Usage in Application class:
 * ```
 * @HiltAndroidApp
 * class MyApplication : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         HiredInSharedLibrary.init(this)
 *     }
 * }
 * ```
 */
object HiredInSharedLibrary {
    private var initialized = false
    private val logger = Logger.getLogger("HiredInSharedLibrary")

    fun init(context: Context, debug: Boolean = true) {
        if (initialized) return
        Logger.init(debug)
        logger.info("HiredIn Shared Library v${VersionInfo.VERSION} initialized")
        initialized = true
    }

    fun isInitialized(): Boolean = initialized
}
