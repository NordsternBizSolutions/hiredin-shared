package com.nordstern.hiredin.shared.notifications

import com.nordstern.hiredin.shared.notifications.models.Notification
import com.nordstern.hiredin.shared.notifications.models.NotificationPayload
import com.nordstern.hiredin.shared.notifications.models.NotificationType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationDataMapper @Inject constructor() {
    fun fromPayload(payload: NotificationPayload): Notification {
        val type = payload.data["type"]?.let {
            runCatching { NotificationType.valueOf(it.uppercase()) }.getOrDefault(NotificationType.GENERAL)
        } ?: NotificationType.GENERAL
        return Notification(
            id = payload.data["notificationId"] ?: payload.data["id"] ?: System.currentTimeMillis().toString(),
            title = payload.data["title"] ?: "HiredIn",
            body = payload.data["body"] ?: "",
            type = type.name.lowercase()
        )
    }

    fun toPayload(notification: Notification): NotificationPayload =
        NotificationPayload(
            data = mapOf(
                "id" to notification.id,
                "title" to notification.title,
                "body" to notification.body.orEmpty(),
                "type" to (notification.type ?: NotificationType.GENERAL.name.lowercase())
            )
        )
}
