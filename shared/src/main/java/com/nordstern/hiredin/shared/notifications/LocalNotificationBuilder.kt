package com.nordstern.hiredin.shared.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.nordstern.hiredin.shared.R
import com.nordstern.hiredin.shared.notifications.channels.ChannelManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalNotificationBuilder @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context,
    private val channelManager: ChannelManager
) {
    fun build(title: String, body: String, type: String): NotificationCompat.Builder {
        channelManager.ensureChannel(type)
        return NotificationCompat.Builder(context, channelManager.getChannelIdForType(type))
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }
}
