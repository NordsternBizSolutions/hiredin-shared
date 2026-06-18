package com.nordstern.hiredin.shared.notifications.channels

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.provider.Settings
import android.os.Build
import com.nordstern.hiredin.shared.notifications.PushNotificationHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelManager @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

    fun ensureChannel(type: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channelId = getChannelIdForType(type)
        if (notificationManager.getNotificationChannel(channelId) != null) return

        val channel = NotificationChannel(
            channelId,
            getChannelNameForType(type),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for $type"
            enableLights(true)
            lightColor = Color.BLUE
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 250, 120, 250)
            val soundUri = Settings.System.DEFAULT_NOTIFICATION_URI
            setSound(
                soundUri,
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun ensureAllChannels() {
        ChannelDefinitions.ALL_TYPES.forEach { ensureChannel(it) }
    }
}
