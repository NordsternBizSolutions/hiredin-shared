package com.nordstern.hiredin.shared.api

import com.google.gson.annotations.SerializedName
import com.nordstern.hiredin.shared.sync.SyncChangesResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): ApiResponse<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ApiResponse<Unit>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<LoginResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ApiResponse<Unit>

    @POST("auth/oauth/{provider}")
    suspend fun oauthLogin(
        @retrofit2.http.Path("provider") provider: String,
        @Body request: OAuthLoginRequest
    ): ApiResponse<LoginResponse>

    @GET("auth/me")
    suspend fun me(): ApiResponse<MeResponse>
}

data class LoginRequest(
    val email: String,
    val password: String,
    val deviceId: String? = null,
    val appModule: String = "candidate",
    val deviceType: String = "android"
)
data class ForgotPasswordRequest(val email: String)
data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val deviceId: String? = null
)
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)
data class OAuthLoginRequest(
    val idToken: String? = null,
    val accessToken: String? = null,
    val authorizationCode: String? = null,
    val deviceId: String? = null
)
data class RefreshRequest(
    val refreshToken: String,
    val deviceId: String? = null
)
data class LoginResponse(
    @SerializedName(value = "accessToken", alternate = ["token", "access_token"])
    val accessToken: String? = null,
    @SerializedName(value = "refreshToken", alternate = ["refresh_token"])
    val refreshToken: String,
    @SerializedName(value = "expiresIn", alternate = ["expires_in"])
    val expiresIn: Long? = null,
    @SerializedName("expiresAt")
    val expiresAt: String? = null,
    @SerializedName(value = "userId", alternate = ["user_id"])
    val userId: String? = null,
    val user: MobileUserDto? = null
) {
    fun resolvedAccessToken(): String =
        accessToken ?: throw IllegalStateException("Missing access token in login response")

    fun resolvedUserId(): String? = userId ?: user?.id

    fun resolvedExpiresInSeconds(): Long {
        expiresIn?.let { return it }
        if (!expiresAt.isNullOrBlank()) {
            return try {
                val expiryMs = java.time.Instant.parse(expiresAt).toEpochMilli()
                ((expiryMs - System.currentTimeMillis()) / 1000).coerceAtLeast(60)
            } catch (_: Exception) {
                DEFAULT_EXPIRES_IN_SECONDS
            }
        }
        return DEFAULT_EXPIRES_IN_SECONDS
    }

    private companion object {
        const val DEFAULT_EXPIRES_IN_SECONDS = 604800L // 7 days
    }
}

data class MobileUserDto(
    val id: String,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val role: String? = null
)
data class MeResponse(
    val userId: String,
    val email: String,
    val role: String,
    val permissions: List<String>? = null
)

interface DeviceApi {
    @POST("devices/register")
    suspend fun register(@Body request: DeviceRegisterRequest): ApiResponse<Unit>
}

data class DeviceRegisterRequest(
    val deviceId: String,
    val deviceType: String = "android",
    val pushToken: String? = null
)
