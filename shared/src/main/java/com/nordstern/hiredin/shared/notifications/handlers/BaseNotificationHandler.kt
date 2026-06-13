package com.nordstern.hiredin.shared.notifications.handlers

import com.nordstern.hiredin.shared.notifications.PushNotificationHandler
import javax.inject.Inject
import javax.inject.Singleton

open class BaseNotificationHandler(
    private val pushHandler: PushNotificationHandler,
    override val type: String
) : NotificationTypeHandler {
    override fun handle(payload: com.nordstern.hiredin.shared.notifications.models.NotificationPayload) {
        pushHandler.showNotification(
            notificationId = payload.data["notificationId"]?.toIntOrNull()
                ?: (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            title = payload.data["title"] ?: "HiredIn",
            body = payload.data["body"] ?: "New notification",
            type = type,
            deepLink = payload.data["deepLink"]
        )
    }
}
