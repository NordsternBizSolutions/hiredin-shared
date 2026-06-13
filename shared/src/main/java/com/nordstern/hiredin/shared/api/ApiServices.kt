package com.nordstern.hiredin.shared.api

import com.nordstern.hiredin.shared.sync.SyncChangesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SyncApi {
    @GET("sync/changes")
    suspend fun getChanges(
        @Query("entities") entities: String? = null,
        @Query("lastSync") lastSync: Long? = null
    ): ApiResponse<SyncChangesResponse>
}

interface AuthApi {
    @retrofit2.http.POST("auth/login")
    suspend fun login(@retrofit2.http.Body request: LoginRequest): ApiResponse<LoginResponse>

    @retrofit2.http.POST("auth/refresh")
    suspend fun refresh(@retrofit2.http.Body request: RefreshRequest): ApiResponse<LoginResponse>

    @retrofit2.http.POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    @retrofit2.http.GET("auth/me")
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
data class MeResponse(val userId: String, val email: String, val role: String, val permissions: List<String>? = null)

interface DeviceApi {
    @retrofit2.http.POST("devices/register")
    suspend fun register(@retrofit2.http.Body request: DeviceRegisterRequest): ApiResponse<Unit>
}

data class DeviceRegisterRequest(
    val deviceId: String,
    val deviceType: String = "android",
    val pushToken: String? = null
)
