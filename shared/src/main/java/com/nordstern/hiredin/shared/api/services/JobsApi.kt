package com.nordstern.hiredin.shared.api.services

import com.google.gson.JsonObject
import com.nordstern.hiredin.shared.api.ApiResponse
import com.nordstern.hiredin.shared.api.JobListItemDto
import com.nordstern.hiredin.shared.build.constants.ApiEndpoints
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface JobsApi {

    @GET(ApiEndpoints.Jobs.SEARCH)
    suspend fun search(
        @Query("q") query: String? = null,
        @Query("location") location: String? = null,
        @Query("jobTypes") jobTypes: String? = null,
        @Query("salaryMin") salaryMin: Int? = null,
        @Query("salaryMax") salaryMax: Int? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("sortBy") sortBy: String? = null
    ): ApiResponse<List<JobListItemDto>>

    @GET(ApiEndpoints.Jobs.DETAIL)
    suspend fun getJob(@Path("jobId") jobId: String): ApiResponse<JobListItemDto>

    @POST(ApiEndpoints.Jobs.APPLY)
    suspend fun apply(
        @Path("jobId") jobId: String,
        @Body body: JobApplyRequest
    ): ApiResponse<JobApplyResponse>

    @POST(ApiEndpoints.Jobs.SAVE)
    suspend fun saveJob(@Body body: SaveJobRequest): ApiResponse<Unit>

    @GET(ApiEndpoints.Jobs.RECOMMENDED)
    suspend fun getRecommended(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("sortBy") sortBy: String = "newest"
    ): ApiResponse<List<JobListItemDto>>

    @GET(ApiEndpoints.Candidate.MATCHING_JOBS)
    suspend fun getMatchingJobs(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null
    ): ApiResponse<com.nordstern.hiredin.shared.api.PaginatedMatchingJobsDto>

    @GET(ApiEndpoints.Jobs.SIMILAR)
    suspend fun getSimilar(
        @Path("jobId") jobId: String,
        @Query("limit") limit: Int = 10
    ): ApiResponse<List<JobListItemDto>>

    @GET(ApiEndpoints.Jobs.FILTERS)
    suspend fun getFilters(): ApiResponse<JobFiltersDto>

    @GET(ApiEndpoints.Jobs.JOB_ALERTS)
    suspend fun getJobAlerts(): ApiResponse<JobAlertsDto>

    @PUT(ApiEndpoints.Jobs.JOB_ALERTS)
    suspend fun updateJobAlerts(@Body body: JobAlertsDto): ApiResponse<JobAlertsDto>
}

data class JobApplyRequest(
    val coverLetter: String? = null,
    val resumeId: String? = null,
    val answers: JsonObject? = null
)

data class SaveJobRequest(val jobId: String, val isSaved: Boolean = false)

data class JobApplyResponse(
    val applicationId: String? = null,
    val id: String? = null,
    val status: String? = null
) {
    fun resolvedApplicationId(): String = applicationId ?: id ?: ""
}

data class JobFiltersDto(
    val locations: List<String> = emptyList(),
    val employmentTypes: List<String> = emptyList(),
    val salaryRanges: List<String> = emptyList()
)

data class JobAlertsDto(
    val enabled: Boolean = true,
    val keywords: String = "",
    val location: String = "",
    val emailDigest: Boolean = true,
    val pushEnabled: Boolean = true
)
