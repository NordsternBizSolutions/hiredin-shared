package com.nordstern.hiredin.shared.models

data class Conversation(
    val id: String,
    val participantName: String,
    val participantUserId: String? = null,
    val participantAvatarUrl: String? = null,
    val lastMessage: String? = null,
    val unreadCount: Int = 0,
    val updatedAt: Long = 0
)

data class Message(
    val id: String,
    val senderId: String,
    val body: String,
    val createdAt: Long,
    val isMine: Boolean = false
)
