package com.nordstern.hiredin.shared.auth.models

data class AuthToken(
    val accessToken: String, val refreshToken: String, val expiresIn: Long
)
