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
    @SerializedName("device_id") val deviceId: String? = null
)
data class ForgotPasswordRequest(val email: String)
data class RegisterRequest(
    val email: String,
    val password: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("device_id") val deviceId: String? = null
)
data class ResetPasswordRequest(
    val token: String,
    @SerializedName("new_password") val newPassword: String
)
data class OAuthLoginRequest(
    @SerializedName("id_token") val idToken: String? = null,
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("authorization_code") val authorizationCode: String? = null,
    @SerializedName("device_id") val deviceId: String? = null
)
data class RefreshRequest(
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("device_id") val deviceId: String? = null
)
data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("user_id") val userId: String? = null
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
