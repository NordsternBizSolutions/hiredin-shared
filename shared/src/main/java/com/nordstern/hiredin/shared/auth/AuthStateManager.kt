package com.nordstern.hiredin.shared.auth

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    fun observeAuthState() = tokenManager.observeAuthState()
}
