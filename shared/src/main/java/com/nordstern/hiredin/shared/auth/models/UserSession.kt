package com.nordstern.hiredin.shared.auth.models

data class UserSession(
    val userId: String, val email: String, val role: String
)
