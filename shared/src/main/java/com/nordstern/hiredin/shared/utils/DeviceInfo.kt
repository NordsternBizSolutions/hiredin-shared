package com.nordstern.hiredin.shared.utils

import android.content.Context
import android.os.Build
import android.provider.Settings

object DeviceInfo {
    fun deviceId(context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"

    fun manufacturer(): String = Build.MANUFACTURER
    fun model(): String = Build.MODEL
    fun osVersion(): String = Build.VERSION.RELEASE
    fun sdkInt(): Int = Build.VERSION.SDK_INT

    fun summary(context: Context): String =
        "${manufacturer()} ${model()} Android ${osVersion()} (${deviceId(context)})"
}
