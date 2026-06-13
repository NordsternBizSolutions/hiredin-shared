package com.nordstern.hiredin.shared.api.services

import com.google.gson.JsonObject
import com.nordstern.hiredin.shared.api.ApiResponse
import com.nordstern.hiredin.shared.build.constants.ApiEndpoints
import com.nordstern.hiredin.shared.models.Application
import com.nordstern.hiredin.shared.models.Job
import com.nordstern.hiredin.shared.models.enums.EmployerTaskStatus
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface EmployerApi {

    @GET(ApiEndpoints.Entity.COMPANY)
    suspend fun getCompany(): ApiResponse<CompanyProfileDto>

    @PUT(ApiEndpoints.Entity.COMPANY)
    suspend fun updateCompany(@Body body: CompanyProfileDto): ApiResponse<CompanyProfileDto>

    @Multipart
    @POST(ApiEndpoints.Entity.COMPANY_LOGO)
    suspend fun uploadLogo(@Part file: MultipartBody.Part): ApiResponse<LogoUploadResponse>

    @GET(ApiEndpoints.Entity.JOBS)
    suspend fun getJobs(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<Job>>

    @POST(ApiEndpoints.Entity.JOBS)
    suspend fun createJob(@Body body: CreateJobRequest): ApiResponse<Job>

    @POST(ApiEndpoints.Entity.JOB_SUBMIT)
    suspend fun submitJob(@Path("id") jobId: String): ApiResponse<Job>

    @POST(ApiEndpoints.Entity.JOB_PUBLISH)
    suspend fun publishJob(@Path("id") jobId: String): ApiResponse<Job>

    @GET(ApiEndpoints.Entity.APPROVAL_QUEUE)
    suspend fun getApprovalQueue(): ApiResponse<List<Job>>

    @GET(ApiEndpoints.Entity.APPLICATIONS)
    suspend fun getApplications(
        @Query("jobId") jobId: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<Application>>

    @GET(ApiEndpoints.Entity.INTERVIEWS)
    suspend fun getInterviews(): ApiResponse<List<EmployerInterviewDto>>

    @GET(ApiEndpoints.Entity.OFFERS)
    suspend fun getOffers(): ApiResponse<List<EmployerOfferDto>>

    @POST(ApiEndpoints.Entity.OFFERS)
    suspend fun createOffer(@Body body: CreateOfferRequest): ApiResponse<EmployerOfferDto>

    @GET(ApiEndpoints.Entity.TEAM)
    suspend fun getTeam(): ApiResponse<List<TeamMemberDto>>

    @GET(ApiEndpoints.Entity.COMPLIANCE)
    suspend fun getComplianceDashboard(): ApiResponse<ComplianceDashboardDto>

    @GET(ApiEndpoints.Entity.TASKS)
    suspend fun getTasks(
        @Query("status") status: EmployerTaskStatus? = null
    ): ApiResponse<List<EmployerTaskDto>>
}

data class CompanyProfileDto(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val industry: String? = null,
    val size: String? = null,
    val website: String? = null,
    val location: String? = null
)

data class LogoUploadResponse(val url: String)

data class CreateJobRequest(
    val title: String,
    val description: String,
    val location: String? = null,
    val employment: String? = null,
    val salaryMin: Int? = null,
    val salaryMax: Int? = null,
    val postingMetadata: JsonObject? = null
)

data class EmployerInterviewDto(
    val id: String,
    val candidateName: String,
    val jobTitle: String,
    val scheduledAt: String,
    val status: String
)

data class EmployerOfferDto(val id: String, val candidateName: String, val jobTitle: String, val status: String)

data class CreateOfferRequest(
    val applicationId: String,
    val salary: Int,
    val startDate: String,
    val letterTemplateId: String? = null
)

data class TeamMemberDto(val id: String, val name: String, val email: String, val role: String)

data class ComplianceDashboardDto(
    val pendingDocuments: Int = 0,
    val expiringSoon: Int = 0,
    val complianceScore: Int = 0
)

data class EmployerTaskDto(
    val id: String,
    val title: String,
    val status: EmployerTaskStatus,
    val dueDate: String? = null
)
