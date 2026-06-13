package com.nordstern.hiredin.shared.notifications

import com.nordstern.hiredin.shared.notifications.channels.ChannelManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationChannelManager @Inject constructor(
    private val channelManager: ChannelManager
) {
    fun ensureChannels() = channelManager.ensureAllChannels()
    fun ensureChannel(type: String) = channelManager.ensureChannel(type)
    fun channelId(type: String): String = channelManager.getChannelIdForType(type)
}
