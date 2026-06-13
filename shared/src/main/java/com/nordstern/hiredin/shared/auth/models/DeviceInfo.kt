package com.nordstern.hiredin.shared.auth.models

data class DeviceInfo(
    val deviceId: String, val deviceType: String = "android"
)
