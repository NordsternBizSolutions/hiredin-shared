package com.nordstern.hiredin.shared.api.services

import com.google.gson.JsonObject
import com.nordstern.hiredin.shared.api.ApiResponse
import com.nordstern.hiredin.shared.build.constants.ApiEndpoints
import com.nordstern.hiredin.shared.models.Application
import com.nordstern.hiredin.shared.models.CandidateProfile
import com.nordstern.hiredin.shared.models.Job
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface CandidateApi {

    @GET(ApiEndpoints.Candidate.PROFILE)
    suspend fun getProfile(): ApiResponse<CandidateProfile>

    @PUT(ApiEndpoints.Candidate.PROFILE)
    suspend fun updateProfile(@Body body: UpdateProfileRequest): ApiResponse<CandidateProfile>

    @GET(ApiEndpoints.Candidate.WORK_EXPERIENCE)
    suspend fun getWorkExperience(): ApiResponse<List<WorkExperienceDto>>

    @POST(ApiEndpoints.Candidate.WORK_EXPERIENCE)
    suspend fun addWorkExperience(@Body body: WorkExperienceDto): ApiResponse<WorkExperienceDto>

    @GET(ApiEndpoints.Candidate.EDUCATION)
    suspend fun getEducation(): ApiResponse<List<EducationDto>>

    @GET(ApiEndpoints.Candidate.SKILLS)
    suspend fun getSkills(): ApiResponse<List<SkillDto>>

    @GET(ApiEndpoints.Candidate.APPLICATIONS_TRACKER)
    suspend fun getApplicationsTracker(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<Application>>

    @POST("candidate/applications/{id}/withdraw")
    suspend fun withdrawApplication(@Path("id") applicationId: String): ApiResponse<Unit>

    @GET(ApiEndpoints.Candidate.SAVED_JOBS)
    suspend fun getSavedJobs(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<Job>>

    @GET(ApiEndpoints.Candidate.DASHBOARD)
    suspend fun getDashboard(): ApiResponse<CandidateDashboardDto>

    @GET(ApiEndpoints.Candidate.CONVERSATIONS)
    suspend fun getConversations(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<ConversationDto>>

    @GET(ApiEndpoints.Candidate.INTERVIEWS)
    suspend fun getInterviews(): ApiResponse<List<InterviewDto>>

    @POST("candidate/interviews/{id}/respond")
    suspend fun respondToInterview(
        @Path("id") interviewId: String,
        @Body body: InterviewResponseRequest
    ): ApiResponse<Unit>

    @GET(ApiEndpoints.Candidate.OFFERS)
    suspend fun getOffers(): ApiResponse<List<OfferDto>>

    @POST("candidate/offers/{id}/respond")
    suspend fun respondToOffer(
        @Path("id") offerId: String,
        @Body body: OfferResponseRequest
    ): ApiResponse<Unit>

    @Multipart
    @POST(ApiEndpoints.Candidate.AVATAR)
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): ApiResponse<AvatarUploadResponse>

    @Multipart
    @POST(ApiEndpoints.Candidate.RESUME)
    suspend fun uploadResume(@Part file: MultipartBody.Part): ApiResponse<ResumeUploadResponse>

    @GET(ApiEndpoints.Candidate.SETTINGS)
    suspend fun getSettings(): ApiResponse<JsonObject>

    @PUT(ApiEndpoints.Candidate.SETTINGS_PASSWORD)
    suspend fun changePassword(@Body body: ChangePasswordRequest): ApiResponse<Unit>
}

data class UpdateProfileRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val headline: String? = null,
    val location: String? = null,
    val yearsExp: Int? = null,
    val expectedSalary: Int? = null,
    val openToWork: Boolean? = null
)

data class WorkExperienceDto(
    val id: String? = null,
    val company: String,
    val title: String,
    val startDate: String,
    val endDate: String? = null,
    val description: String? = null
)

data class EducationDto(
    val id: String? = null,
    val institution: String,
    val degree: String,
    val field: String? = null,
    val startYear: Int? = null,
    val endYear: Int? = null
)

data class SkillDto(val id: String? = null, val name: String, val level: String? = null)

data class CandidateDashboardDto(
    val applicationsCount: Int = 0,
    val interviewsCount: Int = 0,
    val offersCount: Int = 0,
    val profileCompleteness: Int = 0
)

data class ConversationDto(
    val id: String,
    val participantName: String,
    val lastMessage: String? = null,
    val unreadCount: Int = 0
)

data class InterviewDto(
    val id: String,
    val jobTitle: String,
    val scheduledAt: String,
    val status: String
)

data class InterviewResponseRequest(val accepted: Boolean, val message: String? = null)
data class OfferDto(val id: String, val jobTitle: String, val status: String, val expiresAt: String? = null)
data class OfferResponseRequest(val accepted: Boolean, val message: String? = null)
data class AvatarUploadResponse(val url: String)
data class ResumeUploadResponse(val url: String, val parsedData: JsonObject? = null)
data class ChangePasswordRequest(val currentPassword: String, val newPassword: String)
