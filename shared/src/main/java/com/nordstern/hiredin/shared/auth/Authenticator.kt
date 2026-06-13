package com.nordstern.hiredin.shared.auth

import com.nordstern.hiredin.shared.api.AuthApi
import com.nordstern.hiredin.shared.api.BaseApiClient
import com.nordstern.hiredin.shared.api.ForgotPasswordRequest
import com.nordstern.hiredin.shared.api.LoginRequest
import com.nordstern.hiredin.shared.api.OAuthLoginRequest
import com.nordstern.hiredin.shared.api.RegisterRequest
import com.nordstern.hiredin.shared.api.ResetPasswordRequest
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

    suspend fun requestPasswordReset(email: String): Result<Unit> {
        val response = apiClient.safeApiCall {
            apiClient.createService<AuthApi>().forgotPassword(ForgotPasswordRequest(email))
        }
        return if (response.success) {
            logger.info("Password reset requested")
            Result.success(Unit)
        } else {
            Result.failure(Exception(response.error ?: "Could not send reset email"))
        }
    }

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Result<Unit> {
        val deviceId = tokenManager.getDeviceId() ?: UUID.randomUUID().toString().also {
            tokenManager.saveDeviceId(it)
        }
        val response = apiClient.safeApiCall {
            apiClient.createService<AuthApi>().register(
                RegisterRequest(
                    email = email,
                    password = password,
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    deviceId = deviceId
                )
            )
        }
        return if (response.success && response.data != null) {
            tokenManager.saveTokens(
                response.data.accessToken,
                response.data.refreshToken,
                response.data.expiresIn
            )
            response.data.userId?.let { tokenManager.saveUserId(it) }
            sessionManager.onLogin(response.data.userId ?: email)
            logger.info("Registration successful")
            Result.success(Unit)
        } else {
            Result.failure(Exception(response.error ?: "Registration failed"))
        }
    }

    suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        val response = apiClient.safeApiCall {
            apiClient.createService<AuthApi>().resetPassword(
                ResetPasswordRequest(token.trim(), newPassword)
            )
        }
        return if (response.success) {
            logger.info("Password reset successful")
            Result.success(Unit)
        } else {
            Result.failure(Exception(response.error ?: "Password reset failed"))
        }
    }

    suspend fun oauthLogin(
        provider: String,
        idToken: String? = null,
        accessToken: String? = null,
        authorizationCode: String? = null
    ): Result<Unit> {
        val deviceId = tokenManager.getDeviceId() ?: UUID.randomUUID().toString().also {
            tokenManager.saveDeviceId(it)
        }
        val response = apiClient.safeApiCall {
            apiClient.createService<AuthApi>().oauthLogin(
                provider = provider,
                request = OAuthLoginRequest(
                    idToken = idToken,
                    accessToken = accessToken,
                    authorizationCode = authorizationCode,
                    deviceId = deviceId
                )
            )
        }
        return if (response.success && response.data != null) {
            tokenManager.saveTokens(
                response.data.accessToken,
                response.data.refreshToken,
                response.data.expiresIn
            )
            response.data.userId?.let { tokenManager.saveUserId(it) }
            sessionManager.onLogin(response.data.userId ?: provider)
            logger.info("OAuth login successful for $provider")
            Result.success(Unit)
        } else {
            Result.failure(Exception(response.error ?: "Sign in with $provider failed"))
        }
    }
}
