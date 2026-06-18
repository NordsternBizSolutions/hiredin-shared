package com.nordstern.hiredin.shared.notifications.models

import com.google.gson.annotations.SerializedName

data class Notification(
    val id: String,
    val title: String,
    @SerializedName("message", alternate = ["body"])
    val body: String? = null,
    val type: String? = null,
    @SerializedName("actionUrl", alternate = ["deepLink"])
    val deepLink: String? = null,
    val createdAt: String? = null,
    val read: Boolean = false
)
