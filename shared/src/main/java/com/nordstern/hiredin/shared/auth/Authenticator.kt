package com.nordstern.hiredin.shared.auth

import com.nordstern.hiredin.shared.api.AuthApi
import com.nordstern.hiredin.shared.api.BaseApiClient
import com.nordstern.hiredin.shared.api.LoginRequest
import com.nordstern.hiredin.shared.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Authenticator @Inject constructor(
    private val apiClient: BaseApiClient,
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager
) {
    private val logger = Logger.getLogger("Authenticator")

    suspend fun login(email: String, password: String): Result<Unit> {
        val deviceId = tokenManager.getDeviceId() ?: UUID.randomUUID().toString().also {
            tokenManager.saveDeviceId(it)
        }

        val response = apiClient.safeApiCall {
            apiClient.createService<AuthApi>().login(LoginRequest(email, password, deviceId))
        }

        return if (response.success && response.data != null) {
            tokenManager.saveTokens(
                response.data.accessToken,
                response.data.refreshToken,
                response.data.expiresIn
            )
            response.data.userId?.let { tokenManager.saveUserId(it) }
            sessionManager.onLogin(response.data.userId ?: email)
            logger.info("Login successful")
            Result.success(Unit)
        } else {
            logger.warn("Login failed: ${response.error}")
            Result.failure(Exception(response.error ?: "Login failed"))
        }
    }

    suspend fun logout(): Result<Unit> {
        apiClient.safeApiCall { apiClient.createAuthenticatedService<AuthApi>().logout() }
        tokenManager.clearTokens()
        sessionManager.onLogout()
        logger.info("Logout successful")
        return Result.success(Unit)
    }
}

@Singleton
class SessionManager @Inject constructor() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun onLogin(userId: String) {
        _isLoggedIn.value = true
    }

    fun onLogout() {
        _isLoggedIn.value = false
    }
}

@Singleton
class AuthStateManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    fun observeAuthState() = tokenManager.observeAuthState()
}
