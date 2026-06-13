package com.nordstern.hiredin.shared

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.nordstern.hiredin.shared.auth.Authenticator
import com.nordstern.hiredin.shared.auth.TokenManager
import com.nordstern.hiredin.shared.build.VersionInfo
import com.nordstern.hiredin.shared.notifications.PushNotificationHandler
import com.nordstern.hiredin.shared.sync.SyncManager
import com.nordstern.hiredin.shared.utils.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

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

/**
 * Optional base Application class for apps that want built-in WorkManager + Hilt setup.
 */
@HiltAndroidApp
abstract class HiredInApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var authenticator: Authenticator
    @Inject lateinit var tokenManager: TokenManager
    @Inject lateinit var syncManager: SyncManager
    @Inject lateinit var pushNotificationHandler: PushNotificationHandler

    override fun onCreate() {
        super.onCreate()
        HiredInSharedLibrary.init(this, BuildConfig.DEBUG)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
