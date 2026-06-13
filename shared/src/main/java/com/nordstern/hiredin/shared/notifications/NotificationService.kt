package com.nordstern.hiredin.shared.notifications

import android.app.NotificationManager
import android.content.Context
import com.nordstern.hiredin.shared.notifications.handlers.NotificationRouter
import com.nordstern.hiredin.shared.notifications.models.NotificationPayload
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context,
    private val localNotificationBuilder: LocalNotificationBuilder,
    private val router: NotificationRouter
) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun display(payload: NotificationPayload) {
        router.route(payload)
    }

    fun displaySimple(title: String, body: String, type: String = "general", id: Int = 1) {
        val notification = localNotificationBuilder.build(title, body, type).build()
        notificationManager.notify(id, notification)
    }
}
