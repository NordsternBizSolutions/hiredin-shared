package com.nordstern.hiredin.shared.notifications.handlers

import com.nordstern.hiredin.shared.notifications.PushNotificationHandler
import com.nordstern.hiredin.shared.notifications.models.NotificationPayload
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRouter @Inject constructor(
    handlers: Set<@JvmSuppressWildcards NotificationTypeHandler>
) {
    private val handlerMap = handlers.associateBy { it.type }

    fun route(payload: NotificationPayload) {
        val type = payload.data["type"] ?: "general"
        handlerMap[type]?.handle(payload) ?: handlerMap["general"]?.handle(payload)
    }
}
