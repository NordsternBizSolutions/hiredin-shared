package com.nordstern.hiredin.shared.notifications.channels

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.nordstern.hiredin.shared.notifications.PushNotificationHandler
import javax.inject.Inject
import javax.inject.Singleton

object ChannelDefinitions {
    val ALL_TYPES = listOf(
        PushNotificationHandler.NOTIFICATION_TYPE_LEAVE,
        PushNotificationHandler.NOTIFICATION_TYPE_PAYROLL,
        PushNotificationHandler.NOTIFICATION_TYPE_ANNOUNCEMENT,
        PushNotificationHandler.NOTIFICATION_TYPE_TASK,
        PushNotificationHandler.NOTIFICATION_TYPE_APPROVAL,
        PushNotificationHandler.NOTIFICATION_TYPE_COMPLIANCE,
        PushNotificationHandler.NOTIFICATION_TYPE_RECOGNITION,
        "general"
    )
}
