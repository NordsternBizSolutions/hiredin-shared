package com.nordstern.hiredin.shared.notifications.handlers

import com.nordstern.hiredin.shared.notifications.models.NotificationPayload

interface NotificationTypeHandler {
    val type: String
    fun handle(payload: NotificationPayload)
}
