package com.nordstern.hiredin.shared.notifications

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.nordstern.hiredin.shared.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Foreground service that maintains the MQTT push connection.
 */
@AndroidEntryPoint
class PushNotificationService : Service() {

    @Inject lateinit var pushNotificationHandler: PushNotificationHandler

    private val logger = Logger.getLogger("PushNotificationService")

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val brokerUrl = intent?.getStringExtra(EXTRA_BROKER_URL)
        val topic = intent?.getStringExtra(EXTRA_TOPIC)
        val username = intent?.getStringExtra(EXTRA_USERNAME)
        val password = intent?.getStringExtra(EXTRA_PASSWORD)

        if (brokerUrl != null && topic != null) {
            logger.info("Starting MQTT push service")
            pushNotificationHandler.connect(brokerUrl, topic, username, password)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        pushNotificationHandler.disconnect()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val EXTRA_BROKER_URL = "broker_url"
        const val EXTRA_TOPIC = "topic"
        const val EXTRA_USERNAME = "username"
        const val EXTRA_PASSWORD = "password"
    }
}
