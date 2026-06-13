package com.nordstern.hiredin.shared.api

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

    @GET("auth/me")
    suspend fun me(): ApiResponse<MeResponse>
}

data class LoginRequest(val email: String, val password: String, val deviceId: String? = null)
data class RefreshRequest(val refreshToken: String, val deviceId: String? = null)
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val userId: String? = null
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
