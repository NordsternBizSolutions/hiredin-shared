package com.nordstern.hiredin.shared.api.services

import com.google.gson.JsonObject
import com.nordstern.hiredin.shared.api.ApiResponse
import com.nordstern.hiredin.shared.build.constants.ApiEndpoints
import com.nordstern.hiredin.shared.models.Job
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface JobsApi {

    @GET(ApiEndpoints.Jobs.SEARCH)
    suspend fun search(
        @Query("q") query: String? = null,
        @Query("location") location: String? = null,
        @Query("employment") employment: String? = null,
        @Query("salaryMin") salaryMin: Int? = null,
        @Query("salaryMax") salaryMax: Int? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null
    ): ApiResponse<List<Job>>

    @GET(ApiEndpoints.Jobs.DETAIL)
    suspend fun getJob(@Path("id") jobId: String): ApiResponse<Job>

    @POST(ApiEndpoints.Jobs.APPLY)
    suspend fun apply(
        @Path("id") jobId: String,
        @Body body: JobApplyRequest
    ): ApiResponse<JobApplyResponse>

    @POST(ApiEndpoints.Jobs.SAVE)
    suspend fun saveJob(@Path("id") jobId: String): ApiResponse<Unit>

    @POST(ApiEndpoints.Jobs.UNSAVE)
    suspend fun unsaveJob(@Path("id") jobId: String): ApiResponse<Unit>

    @GET(ApiEndpoints.Jobs.RECOMMENDED)
    suspend fun getRecommended(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<Job>>

    @GET(ApiEndpoints.Jobs.SIMILAR)
    suspend fun getSimilar(
        @Path("id") jobId: String,
        @Query("limit") limit: Int = 10
    ): ApiResponse<List<Job>>

    @GET(ApiEndpoints.Jobs.FILTERS)
    suspend fun getFilters(): ApiResponse<JobFiltersDto>
}

data class JobApplyRequest(
    val coverLetter: String? = null,
    val resumeId: String? = null,
    val answers: JsonObject? = null
)

data class JobApplyResponse(val applicationId: String, val status: String)

data class JobFiltersDto(
    val locations: List<String> = emptyList(),
    val employmentTypes: List<String> = emptyList(),
    val salaryRanges: List<String> = emptyList()
)
