package com.nordstern.hiredin.shared.notifications.channels

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.nordstern.hiredin.shared.notifications.PushNotificationHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelUpdater @Inject constructor(private val channelManager: ChannelManager) {
    fun updateChannels() = channelManager.ensureAllChannels()
}
