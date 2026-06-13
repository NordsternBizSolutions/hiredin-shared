package com.nordstern.hiredin.shared.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.nordstern.hiredin.shared.R
import com.nordstern.hiredin.shared.api.BaseApiClient
import com.nordstern.hiredin.shared.api.DeviceApi
import com.nordstern.hiredin.shared.api.DeviceRegisterRequest
import com.nordstern.hiredin.shared.auth.TokenManager
import com.nordstern.hiredin.shared.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enterprise push transport using Eclipse Paho MQTT (open-source).
 * Replaces Firebase Cloud Messaging with a self-hosted or managed MQTT broker.
 */
@Singleton
class PushNotificationHandler @Inject constructor(
    private val context: Context,
    private val notificationChannelManager: NotificationChannelManager,
    private val apiClient: BaseApiClient,
    private val tokenManager: TokenManager
) {
    private val logger = Logger.getLogger("PushNotificationHandler")
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var mqttClient: MqttClient? = null

    companion object {
        const val NOTIFICATION_TYPE_LEAVE = "leave"
        const val NOTIFICATION_TYPE_PAYROLL = "payroll"
        const val NOTIFICATION_TYPE_ANNOUNCEMENT = "announcement"
        const val NOTIFICATION_TYPE_TASK = "task"
        const val NOTIFICATION_TYPE_APPROVAL = "approval"
        const val NOTIFICATION_TYPE_COMPLIANCE = "compliance"
        const val NOTIFICATION_TYPE_RECOGNITION = "recognition"
        private const val NOTIFICATION_ID_BASE = 1000
    }

    fun connect(brokerUrl: String, topic: String, username: String? = null, password: String? = null) {
        scope.launch {
            try {
                val clientId = tokenManager.getDeviceId() ?: MqttClient.generateClientId()
                mqttClient = MqttClient(brokerUrl, clientId, MemoryPersistence()).apply {
                    setCallback(object : MqttCallbackExtended {
                        override fun connectComplete(reconnect: Boolean, serverUri: String?) {
                            logger.info("MQTT connected: $serverUri (reconnect=$reconnect)")
                            subscribe(topic, 1)
                        }

                        override fun connectionLost(cause: Throwable?) {
                            logger.warn("MQTT connection lost", cause)
                        }

                        override fun messageArrived(topic: String?, message: MqttMessage?) {
                            message?.let { handleMqttMessage(String(it.payload)) }
                        }

                        override fun deliveryComplete(token: IMqttDeliveryToken?) {}
                    })

                    val options = MqttConnectOptions().apply {
                        isAutomaticReconnect = true
                        isCleanSession = false
                        connectionTimeout = 30
                        keepAliveInterval = 60
                        username?.let { userName = it }
                        password?.let { this.password = it.toCharArray() }
                    }
                    connect(options)
                }
            } catch (e: Exception) {
                logger.error("MQTT connection failed", e)
            }
        }
    }

    fun disconnect() {
        try {
            mqttClient?.disconnect()
            mqttClient?.close()
            mqttClient = null
        } catch (e: Exception) {
            logger.error("MQTT disconnect failed", e)
        }
    }

    fun handleMqttMessage(payload: String) {
        try {
            val json = JSONObject(payload)
            val type = json.optString("type", "general")
            val title = json.optString("title", "HiredIn Notification")
            val body = json.optString("body", "You have a new notification")
            val notificationId = json.optString("notificationId").toIntOrNull() ?: generateNotificationId()
            val deepLink = json.optString("deepLink").takeIf { it.isNotEmpty() }

            showNotification(notificationId, title, body, type, deepLink)
        } catch (e: Exception) {
            logger.error("Failed to parse MQTT message", e)
        }
    }

    fun showNotification(
        notificationId: Int,
        title: String,
        body: String,
        type: String,
        deepLink: String? = null
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = notificationChannelManager.getChannelIdForType(type)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                notificationChannelManager.getChannelNameForType(type),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for $type"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            deepLink?.let { data = android.net.Uri.parse(it) }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(getNotificationColor(type))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    suspend fun registerPushToken(deviceToken: String) {
        val deviceId = tokenManager.getDeviceId() ?: return
        apiClient.safeApiCall {
            apiClient.createAuthenticatedService<DeviceApi>().register(
                DeviceRegisterRequest(deviceId = deviceId, pushToken = deviceToken)
            )
        }
        logger.info("Push token registered with server")
    }

    private fun getNotificationColor(type: String): Int = when (type) {
        NOTIFICATION_TYPE_LEAVE -> Color.parseColor("#4CAF50")
        NOTIFICATION_TYPE_PAYROLL -> Color.parseColor("#2196F3")
        NOTIFICATION_TYPE_ANNOUNCEMENT -> Color.parseColor("#FF9800")
        NOTIFICATION_TYPE_TASK -> Color.parseColor("#9C27B0")
        NOTIFICATION_TYPE_APPROVAL -> Color.parseColor("#F44336")
        NOTIFICATION_TYPE_COMPLIANCE -> Color.parseColor("#FF5722")
        NOTIFICATION_TYPE_RECOGNITION -> Color.parseColor("#E91E63")
        else -> Color.parseColor("#3F51B5")
    }

    private fun generateNotificationId(): Int =
        NOTIFICATION_ID_BASE + (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
}

@Singleton
class NotificationChannelManager @Inject constructor() {
    fun getChannelIdForType(type: String): String = "hiredin_channel_$type"

    fun getChannelNameForType(type: String): String = when (type) {
        PushNotificationHandler.NOTIFICATION_TYPE_LEAVE -> "Leave Notifications"
        PushNotificationHandler.NOTIFICATION_TYPE_PAYROLL -> "Payroll Notifications"
        PushNotificationHandler.NOTIFICATION_TYPE_ANNOUNCEMENT -> "Announcements"
        PushNotificationHandler.NOTIFICATION_TYPE_TASK -> "Task Notifications"
        PushNotificationHandler.NOTIFICATION_TYPE_APPROVAL -> "Approval Notifications"
        PushNotificationHandler.NOTIFICATION_TYPE_COMPLIANCE -> "Compliance Alerts"
        PushNotificationHandler.NOTIFICATION_TYPE_RECOGNITION -> "Recognition & Rewards"
        else -> "General Notifications"
    }
}
