package com.nordstern.hiredin.shared.api

import com.nordstern.hiredin.shared.api.services.CandidateApi
import com.nordstern.hiredin.shared.api.services.EmployerApi
import com.nordstern.hiredin.shared.api.services.EssApi
import com.nordstern.hiredin.shared.api.services.HrmsApi
import com.nordstern.hiredin.shared.api.services.JobsApi
import com.nordstern.hiredin.shared.api.services.NotificationsApi
import com.nordstern.hiredin.shared.sync.SyncChangesResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central registry for all HiredIn mobile API services.
 * Consuming apps inject this and access module-specific Retrofit interfaces.
 */
@Singleton
class HiredInApiRegistry @Inject constructor(
    private val apiClient: BaseApiClient
) {
    val auth: AuthApi get() = apiClient.createService()
    val sync: SyncApi get() = apiClient.createAuthenticatedService()
    val devices: DeviceApi get() = apiClient.createAuthenticatedService()
    val candidate: CandidateApi get() = apiClient.createAuthenticatedService()
    val jobs: JobsApi get() = apiClient.createAuthenticatedService()
    val employer: EmployerApi get() = apiClient.createAuthenticatedService()
    val hrms: HrmsApi get() = apiClient.createAuthenticatedService()
    val ess: EssApi get() = apiClient.createAuthenticatedService()
    val notifications: NotificationsApi get() = apiClient.createAuthenticatedService()

    suspend fun <T> call(block: suspend () -> ApiResponse<T>): ApiResponse<T> =
        apiClient.safeApiCall(apiCall = block)
}

// ── Expanded sync + locale endpoints ─────────────────────────────────────────

interface SyncApi {
    @GET("sync/changes")
    suspend fun getChanges(
        @Query("entities") entities: String? = null,
        @Query("lastSync") lastSync: Long? = null
    ): ApiResponse<SyncChangesResponse>

    @POST("sync/queue")
    suspend fun queueOfflineAction(@Body request: OfflineQueueRequest): ApiResponse<OfflineQueueResponse>

    @GET("sync/pending")
    suspend fun getPendingActions(): ApiResponse<List<PendingSyncActionDto>>
}

data class OfflineQueueRequest(
    val actionType: String,
    val endpoint: String,
    val method: String,
    val payload: Any,
    val headers: Map<String, String>? = null
)

data class OfflineQueueResponse(val actionId: String, val status: String)

data class PendingSyncActionDto(
    val id: String,
    val actionType: String,
    val endpoint: String,
    val status: String,
    val createdAt: String
)

interface LocaleApi {
    @GET("locale")
    suspend fun getLocale(): ApiResponse<LocaleDto>
}

data class LocaleDto(
    val language: String,
    val region: String,
    val currency: String,
    val dateFormat: String,
    val rtl: Boolean = false
)
