package com.nordstern.hiredin.shared.api.services

import com.google.gson.JsonObject
import com.nordstern.hiredin.shared.api.ApiResponse
import com.nordstern.hiredin.shared.api.ApplicationListItemDto
import com.nordstern.hiredin.shared.api.CandidateProfileResponseDto
import com.nordstern.hiredin.shared.api.ConversationListItemDto
import com.nordstern.hiredin.shared.api.ContractsListDto
import com.nordstern.hiredin.shared.api.DashboardStatsDto
import com.nordstern.hiredin.shared.build.constants.ApiEndpoints
import com.nordstern.hiredin.shared.api.FlexibleIdDeserializer
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
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
    suspend fun getProfile(): ApiResponse<CandidateProfileResponseDto>

    @PUT(ApiEndpoints.Candidate.PROFILE)
    suspend fun updateProfile(@Body body: UpdateProfileRequest): ApiResponse<CandidateProfile>

    @GET(ApiEndpoints.Candidate.WORK_EXPERIENCE)
    suspend fun getWorkExperience(): ApiResponse<List<WorkExperienceDto>>

    @POST(ApiEndpoints.Candidate.WORK_EXPERIENCE)
    suspend fun addWorkExperience(@Body body: WorkExperienceDto): ApiResponse<WorkExperienceDto>

    @PUT("${ApiEndpoints.Candidate.WORK_EXPERIENCE}/{id}")
    suspend fun updateWorkExperience(
        @Path("id") id: String,
        @Body body: WorkExperienceDto
    ): ApiResponse<WorkExperienceDto>

    @DELETE("${ApiEndpoints.Candidate.WORK_EXPERIENCE}/{id}")
    suspend fun deleteWorkExperience(@Path("id") id: String): ApiResponse<Unit>

    @GET(ApiEndpoints.Candidate.EDUCATION)
    suspend fun getEducation(): ApiResponse<List<EducationDto>>

    @POST(ApiEndpoints.Candidate.EDUCATION)
    suspend fun addEducation(@Body body: EducationDto): ApiResponse<EducationDto>

    @PUT("${ApiEndpoints.Candidate.EDUCATION}/{id}")
    suspend fun updateEducation(@Path("id") id: String, @Body body: EducationDto): ApiResponse<EducationDto>

    @DELETE("${ApiEndpoints.Candidate.EDUCATION}/{id}")
    suspend fun deleteEducation(@Path("id") id: String): ApiResponse<Unit>

    @GET(ApiEndpoints.Candidate.SKILLS)
    suspend fun getSkills(): ApiResponse<List<SkillDto>>

    @POST(ApiEndpoints.Candidate.SKILLS)
    suspend fun addSkill(@Body body: SkillDto): ApiResponse<SkillDto>

    @PUT("${ApiEndpoints.Candidate.SKILLS}/{id}")
    suspend fun updateSkill(@Path("id") id: String, @Body body: SkillDto): ApiResponse<SkillDto>

    @DELETE("${ApiEndpoints.Candidate.SKILLS}/{id}")
    suspend fun deleteSkill(@Path("id") id: String): ApiResponse<Unit>

    @GET(ApiEndpoints.Candidate.LANGUAGES)
    suspend fun getLanguages(): ApiResponse<List<LanguageDto>>

    @POST(ApiEndpoints.Candidate.LANGUAGES)
    suspend fun addLanguage(@Body body: LanguageDto): ApiResponse<LanguageDto>

    @PUT("${ApiEndpoints.Candidate.LANGUAGES}/{id}")
    suspend fun updateLanguage(@Path("id") id: String, @Body body: LanguageDto): ApiResponse<LanguageDto>

    @DELETE("${ApiEndpoints.Candidate.LANGUAGES}/{id}")
    suspend fun deleteLanguage(@Path("id") id: String): ApiResponse<Unit>

    @GET(ApiEndpoints.Candidate.CERTIFICATIONS)
    suspend fun getCertifications(): ApiResponse<List<CertificationDto>>

    @POST(ApiEndpoints.Candidate.CERTIFICATIONS)
    suspend fun addCertification(@Body body: CertificationDto): ApiResponse<CertificationDto>

    @PUT("${ApiEndpoints.Candidate.CERTIFICATIONS}/{id}")
    suspend fun updateCertification(
        @Path("id") id: String,
        @Body body: CertificationDto
    ): ApiResponse<CertificationDto>

    @DELETE("${ApiEndpoints.Candidate.CERTIFICATIONS}/{id}")
    suspend fun deleteCertification(@Path("id") id: String): ApiResponse<Unit>

    @GET(ApiEndpoints.Candidate.SOCIAL_LINKS)
    suspend fun getSocialLinks(): ApiResponse<List<SocialLinkDto>>

    @POST(ApiEndpoints.Candidate.SOCIAL_LINKS)
    suspend fun addSocialLink(@Body body: SocialLinkDto): ApiResponse<SocialLinkDto>

    @PUT("${ApiEndpoints.Candidate.SOCIAL_LINKS}/{id}")
    suspend fun updateSocialLink(@Path("id") id: String, @Body body: SocialLinkDto): ApiResponse<SocialLinkDto>

    @DELETE("${ApiEndpoints.Candidate.SOCIAL_LINKS}/{id}")
    suspend fun deleteSocialLink(@Path("id") id: String): ApiResponse<Unit>

    @GET(ApiEndpoints.Candidate.CAREER_HUB)
    suspend fun getCareerHub(): ApiResponse<CareerHubDto>

    @PUT(ApiEndpoints.Candidate.CAREER_HUB)
    suspend fun updateCareerHub(@Body body: UpdateCareerHubRequest): ApiResponse<JsonObject>

    @GET(ApiEndpoints.Candidate.SMARTCV)
    suspend fun getSmartCv(): ApiResponse<SmartCvResponseDto>

    @PUT(ApiEndpoints.Candidate.SMARTCV)
    suspend fun updateSmartCv(@Body body: JsonObject): ApiResponse<JsonObject>

    @GET(ApiEndpoints.Candidate.ACHIEVEMENTS)
    suspend fun getAchievements(): ApiResponse<List<AchievementDto>>

    @GET(ApiEndpoints.Candidate.RESUME_PARSE_STATUS)
    suspend fun getResumeParseStatus(@Path("id") resumeId: String): ApiResponse<ResumeParseStatusDto>

    @Multipart
    @POST(ApiEndpoints.Candidate.SMARTCV_DOCUMENTS)
    suspend fun uploadSmartCvDocument(
        @Part file: MultipartBody.Part,
        @Part("type") type: RequestBody
    ): ApiResponse<JsonObject>

    @GET(ApiEndpoints.Candidate.APPLICATIONS_TRACKER)
    suspend fun getApplicationsTracker(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<ApplicationListItemDto>>

    @POST("candidate/applications/{id}/withdraw")
    suspend fun withdrawApplication(@Path("id") applicationId: String): ApiResponse<Unit>

    @GET(ApiEndpoints.Candidate.SAVED_JOBS)
    suspend fun getSavedJobs(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<Job>>

    @GET(ApiEndpoints.Candidate.DASHBOARD)
    suspend fun getDashboard(): ApiResponse<DashboardStatsDto>

    @GET(ApiEndpoints.Candidate.DASHBOARD_JOURNEY)
    suspend fun getJourneyOverview(): ApiResponse<com.nordstern.hiredin.shared.api.JourneyOverviewDto>

    @GET(ApiEndpoints.Candidate.DASHBOARD_APPLICATION_TREND)
    suspend fun getApplicationTrend(
        @Query("days") days: Int = 30
    ): ApiResponse<List<com.nordstern.hiredin.shared.api.ApplicationTrendDto>>

    @GET(ApiEndpoints.Candidate.DASHBOARD_JOB_SOURCES)
    suspend fun getJobSources(): ApiResponse<List<com.nordstern.hiredin.shared.api.JobSourceDto>>

    @GET(ApiEndpoints.Candidate.UPCOMING_INTERVIEWS)
    suspend fun getUpcomingInterviews(): ApiResponse<List<com.nordstern.hiredin.shared.api.UpcomingInterviewDto>>

    @GET(ApiEndpoints.Candidate.RECOMMENDED_VIDEOS)
    suspend fun getRecommendedVideos(
        @Query("limit") limit: Int = 10
    ): ApiResponse<List<com.nordstern.hiredin.shared.api.CareerVideoDto>>

    @GET(ApiEndpoints.Candidate.RECENT_ACTIVITIES)
    suspend fun getRecentActivities(
        @Query("limit") limit: Int = 10
    ): ApiResponse<List<com.nordstern.hiredin.shared.api.RecentActivityDto>>

    @GET(ApiEndpoints.Candidate.CANDIDATE_SKILLS)
    suspend fun getCandidateSkills(): ApiResponse<List<com.nordstern.hiredin.shared.api.CandidateSkillDto>>

    @GET(ApiEndpoints.Candidate.MARKET_SALARY)
    suspend fun getMarketSalary(
        @Query("title") title: String,
        @Query("location") location: String,
        @Query("experience") experience: String
    ): ApiResponse<com.nordstern.hiredin.shared.api.MarketSalaryDto>

    @GET(ApiEndpoints.Candidate.SALARY_EXPECTATION)
    suspend fun getSalaryExpectation(): ApiResponse<com.nordstern.hiredin.shared.api.SalaryExpectationDto>

    @GET(ApiEndpoints.Candidate.COMPANIES_RECOMMENDED)
    suspend fun getRecommendedCompanies(
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 1
    ): ApiResponse<List<com.nordstern.hiredin.shared.api.CompanyListItemDto>>

    @GET(ApiEndpoints.Candidate.COMPANIES_FOLLOWING)
    suspend fun getFollowingCompanies(): ApiResponse<List<com.nordstern.hiredin.shared.api.CompanyListItemDto>>

    @GET(ApiEndpoints.Candidate.COMPANIES_TRENDING)
    suspend fun getTrendingCompanies(): ApiResponse<List<com.nordstern.hiredin.shared.api.CompanyListItemDto>>

    @GET(ApiEndpoints.Candidate.COMPANIES_FOLLOWING_IDS)
    suspend fun getFollowingCompanyIds(): ApiResponse<List<String>>

    @POST(ApiEndpoints.Candidate.COMPANY_FOLLOW)
    suspend fun followCompany(@Path("companyId") companyId: String): ApiResponse<Unit>

    @DELETE(ApiEndpoints.Candidate.COMPANY_FOLLOW)
    suspend fun unfollowCompany(@Path("companyId") companyId: String): ApiResponse<Unit>

    @GET(ApiEndpoints.Candidate.COMPANY_DETAIL)
    suspend fun getCompanyDetail(
        @Path("companyId") companyId: String
    ): ApiResponse<com.nordstern.hiredin.shared.api.CompanyListItemDto>

    @GET(ApiEndpoints.Candidate.COMPANY_REVIEWS)
    suspend fun getCompanyReviews(
        @Path("companyId") companyId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<com.nordstern.hiredin.shared.api.CompanyReviewsResponseDto>

    @POST(ApiEndpoints.Candidate.COMPANY_REVIEWS)
    suspend fun submitCompanyReview(
        @Path("companyId") companyId: String,
        @Body body: com.nordstern.hiredin.shared.api.SubmitCompanyReviewRequest
    ): ApiResponse<com.nordstern.hiredin.shared.api.CompanyReviewDto>

    @GET(ApiEndpoints.Candidate.RESUMES)
    suspend fun getResumes(): ApiResponse<List<com.nordstern.hiredin.shared.api.ResumeListItemDto>>

    @POST(ApiEndpoints.Candidate.QUICK_APPLY)
    suspend fun quickApply(
        @Path("jobId") jobId: String,
        @Body body: QuickApplyRequest
    ): ApiResponse<QuickApplyResponse>

    @GET(ApiEndpoints.Candidate.CONVERSATIONS)
    suspend fun getConversations(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<ConversationListItemDto>>

    @GET(ApiEndpoints.Candidate.INTERVIEWS)
    suspend fun getInterviews(): ApiResponse<CandidateInterviewsListDto>

    @GET(ApiEndpoints.Candidate.WEB_INTERVIEWS)
    suspend fun getInterviewsFromWebPortal(): ApiResponse<CandidateInterviewsListDto>

    @GET(ApiEndpoints.Candidate.WEB_UPCOMING_INTERVIEWS)
    suspend fun getUpcomingInterviewsFromWebPortal(): ApiResponse<List<com.nordstern.hiredin.shared.api.UpcomingInterviewDto>>

    @POST("candidate/interviews/{id}/respond")
    suspend fun respondToInterview(
        @Path("id") interviewId: String,
        @Body body: InterviewResponseRequest
    ): ApiResponse<Unit>

    @POST(ApiEndpoints.Candidate.WEB_INTERVIEW_RESPOND)
    suspend fun respondToInterviewWebPortal(
        @Path("id") interviewId: String,
        @Body body: InterviewResponseRequest
    ): ApiResponse<Unit>

    @GET(ApiEndpoints.Candidate.INTERVIEW_DAILY_JOIN)
    suspend fun getInterviewDailyJoin(
        @Path("id") interviewId: String
    ): ApiResponse<DailyJoinDto>

    @GET(ApiEndpoints.Candidate.WEB_INTERVIEW_DAILY_JOIN)
    suspend fun getInterviewDailyJoinWebPortal(
        @Path("id") interviewId: String
    ): ApiResponse<DailyJoinDto>

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
    suspend fun uploadResume(@Part resume: MultipartBody.Part): ApiResponse<CandidateResumeDto>

    @GET(ApiEndpoints.Candidate.SETTINGS)
    suspend fun getSettings(): ApiResponse<JsonObject>

    @PUT(ApiEndpoints.Candidate.SETTINGS_PASSWORD)
    suspend fun changePassword(@Body body: ChangePasswordRequest): ApiResponse<Unit>

    @GET(ApiEndpoints.Candidate.CONTRACTS)
    suspend fun getContracts(): ApiResponse<ContractsListDto>
}

data class UpdateProfileRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val headline: String? = null,
    val location: String? = null,
    val yearsExp: Int? = null,
    val expectedSalary: Int? = null,
    val currentSalary: Int? = null,
    val visaStatus: com.nordstern.hiredin.shared.models.enums.CandidateVisaStatus? = null,
    val openToWork: Boolean? = null
)

data class WorkExperienceDto(
    val id: String? = null,
    @SerializedName("company") val company: String = "",
    @SerializedName("companyName") val companyName: String? = null,
    @SerializedName("title") val title: String = "",
    @SerializedName("jobTitle") val jobTitle: String? = null,
    val startDate: String = "",
    val endDate: String? = null,
    val description: String? = null
)

data class EducationDto(
    val id: String? = null,
    val institution: String = "",
    val degree: String = "",
    @SerializedName("field") val field: String? = null,
    @SerializedName("fieldOfStudy") val fieldOfStudy: String? = null,
    val startYear: Int? = null,
    val endYear: Int? = null,
    val startDate: String? = null,
    val endDate: String? = null
)

data class SkillDto(
    val id: String? = null,
    val name: String = "",
    @SerializedName("level") val level: String? = null,
    @SerializedName("proficiency") val proficiency: String? = null
)

data class LanguageDto(
    val id: String? = null,
    val language: String = "",
    val proficiency: String? = null
)

data class CertificationDto(
    val id: String? = null,
    val name: String = "",
    @SerializedName("issuer") val issuer: String? = null,
    @SerializedName("issuingOrganization") val issuingOrganization: String? = null,
    val issueDate: String? = null,
    val expiryDate: String? = null,
    val credentialUrl: String? = null
)

data class SocialLinkDto(
    val id: String? = null,
    @SerializedName("platform", alternate = ["type", "name"]) val platform: String = "",
    @SerializedName("url", alternate = ["link", "href"]) val url: String = ""
)

data class CareerHubDto(
    val profileCompleteness: Int = 0,
    val workExperienceCount: Int = 0,
    val educationCount: Int = 0,
    val skillsCount: Int = 0,
    val certificationsCount: Int = 0,
    val socialLinksCount: Int = 0,
    val languagesCount: Int = 0
)

data class CandidateDashboardDto(
    val applicationsCount: Int = 0,
    val interviewsCount: Int = 0,
    val offersCount: Int = 0,
    val profileCompleteness: Int = 0
)

data class ConversationDto(
    val id: String,
    val participantName: String,
    val participantUserId: String? = null,
    val lastMessage: String? = null,
    val unreadCount: Int = 0
)

data class InterviewJobEmbedDto(
    val id: String? = null,
    val title: String? = null,
    @SerializedName("company_name", alternate = ["companyName"]) val companyName: String? = null,
    val company: com.nordstern.hiredin.shared.api.CompanyBriefDto? = null
)

/** GET candidate/interviews — upcoming + history buckets. */
data class CandidateInterviewsListDto(
    val upcoming: List<InterviewDto> = emptyList(),
    val history: List<InterviewDto> = emptyList(),
    @SerializedName("pending_count", alternate = ["pendingCount"]) val pendingCount: Int = 0,
    val items: List<InterviewDto>? = null
) {
    fun allInterviews(): List<InterviewDto> = upcoming + history + items.orEmpty()
}

data class InterviewDto(
    @JsonAdapter(FlexibleIdDeserializer::class)
    @SerializedName("id", alternate = ["interviewId", "_id"]) val id: String,
    @SerializedName("job_id", alternate = ["jobId"]) val jobId: String? = null,
    @SerializedName("job_title", alternate = ["jobTitle"]) val jobTitle: String? = null,
    @SerializedName("company_name", alternate = ["companyName"]) val companyName: String? = null,
    @SerializedName("company_logo", alternate = ["companyLogo", "logo_url", "logoUrl", "logo"]) val companyLogo: String? = null,
    val job: InterviewJobEmbedDto? = null,
    val company: com.nordstern.hiredin.shared.api.CompanyBriefDto? = null,
    @SerializedName("interview_type", alternate = ["interviewType", "type"]) val interviewType: String? = null,
    @SerializedName("interviewer_name", alternate = ["interviewerName"]) val interviewerName: String? = null,
    @SerializedName("interviewer_title", alternate = ["interviewerTitle"]) val interviewerTitle: String? = null,
    @SerializedName("duration_minutes", alternate = ["durationMinutes", "duration"]) val durationMinutes: Int = 45,
    @SerializedName("scheduled_date", alternate = ["scheduledDate"]) val scheduledDate: String? = null,
    @SerializedName("scheduled_time", alternate = ["scheduledTime"]) val scheduledTime: String? = null,
    @SerializedName("scheduled_at", alternate = ["scheduledAt"]) val scheduledAt: String? = null,
    val timezone: String? = null,
    @SerializedName("meeting_link", alternate = ["meetingLink"]) val meetingLink: String? = null,
    @SerializedName("is_virtual", alternate = ["isVirtual"]) val isVirtual: Boolean = true,
    @SerializedName("is_upcoming", alternate = ["isUpcoming"]) val isUpcoming: Boolean? = null,
    @SerializedName("invitation_response", alternate = ["invitationResponse"]) val invitationResponse: String? = null,
    @SerializedName("cancelled_reason", alternate = ["cancelledReason"]) val cancelledReason: String? = null,
    val location: String? = null,
    val status: String = ""
) {
    fun resolvedScheduleLabel(): String {
        scheduledAt?.takeIf { it.isNotBlank() }?.let { return it }
        return listOfNotNull(scheduledDate, scheduledTime).joinToString(" • ").ifBlank { scheduledAt.orEmpty() }
    }

    fun resolvedJobTitle(): String =
        jobTitle.orEmpty().ifBlank { job?.title.orEmpty() }.ifBlank { "Interview" }

    fun resolvedJobId(): String? = jobId?.takeIf { it.isNotBlank() } ?: job?.id

    fun resolvedCompanyName(): String? =
        companyName?.takeIf { it.isNotBlank() }
            ?: job?.companyName?.takeIf { it.isNotBlank() }
            ?: job?.company?.name?.takeIf { it.isNotBlank() }
            ?: company?.name?.takeIf { it.isNotBlank() }

    fun resolvedCompanyLogo(): String? =
        companyLogo?.takeIf { it.isNotBlank() }
            ?: job?.company?.logoUrl?.takeIf { it.isNotBlank() }
            ?: company?.logoUrl?.takeIf { it.isNotBlank() }

    fun resolvedDurationMinutes(): Int = durationMinutes.takeIf { it > 0 } ?: 45

    fun resolvedIsVirtual(): Boolean {
        if (isVirtual) return true
        val type = interviewType?.trim()?.uppercase().orEmpty()
        return type in setOf("VIDEO", "VIRTUAL", "ONLINE", "REMOTE") || type.contains("VIDEO")
    }

    fun resolvedStatus(fromHistory: Boolean = false): String {
        normalizeTerminalStatus(status)?.let { return it }

        if (fromHistory || isUpcoming == false) {
            return status.ifBlank { "completed" }
        }

        invitationResponse?.takeIf { it.isNotBlank() }?.let { response ->
            when (response.trim().uppercase()) {
                "PENDING" -> return "pending"
                "ACCEPTED" -> return status.ifBlank { "scheduled" }
                "DECLINED", "REJECTED" -> return "declined"
            }
        }
        return status
    }

    private fun normalizeTerminalStatus(raw: String): String? {
        when (raw.trim().uppercase().replace(' ', '_')) {
            "COMPLETED", "COMPLETE" -> return "completed"
            "CANCELLED", "CANCELED" -> return "cancelled"
            "DECLINED", "REJECTED" -> return "declined"
            "NO_SHOW", "NO-SHOW" -> return "no_show"
            "MISSED" -> return "missed"
            "EXPIRED" -> return "expired"
        }
        return null
    }
}

data class InterviewResponseRequest(val accepted: Boolean, val message: String? = null)

data class DailyJoinDto(
    @SerializedName("room_url", alternate = ["roomUrl", "url"]) val roomUrl: String? = null,
    @SerializedName("meeting_token", alternate = ["meetingToken", "token"]) val token: String? = null,
    @SerializedName("room_name", alternate = ["roomName"]) val roomName: String? = null,
    @SerializedName("user_name", alternate = ["userName", "displayName"]) val userName: String? = null,
    @SerializedName("expires_at", alternate = ["expiresAt"]) val expiresAt: String? = null
)
data class OfferDto(val id: String, val jobTitle: String, val status: String, val expiresAt: String? = null)
data class OfferResponseRequest(val accepted: Boolean, val message: String? = null)
data class AvatarUploadResponse(val url: String)

data class UpdateCareerHubRequest(
    val openToWork: Boolean? = null,
    val openToWorkVisibility: String? = null
)

data class AchievementDto(
    val id: String? = null,
    val title: String,
    val description: String? = null,
    val issuer: String? = null,
    val achievedAt: String? = null
)

data class SmartCvDocumentEntryDto(
    val url: String? = null,
    val fileName: String? = null,
    val uploadedAt: String? = null
)

data class SmartCvProfileDto(
    val visaStatus: String? = null,
    val visaStatusLabel: String? = null,
    val visaExpiryDate: String? = null,
    val hasDriverLicense: Boolean = false,
    val driverLicenseExpiry: String? = null,
    @SerializedName("openToWork", alternate = ["open_to_work", "isOpenToWork", "is_open_to_work"])
    val openToWork: Boolean = false,
    val resumeUrl: String? = null,
    val documents: Map<String, SmartCvDocumentEntryDto>? = null
)

data class SmartCvResponseDto(
    val profile: SmartCvProfileDto? = null
)

data class ResumeParseStatusDto(
    val status: String? = null,
    val error: String? = null,
    val polling: Boolean = false,
    val parsedSummary: ResumeParseSummaryDto? = null
)

data class ResumeParseSummaryDto(
    val experienceCount: Int = 0,
    val educationCount: Int = 0,
    val skillsCount: Int = 0
)

data class CandidateResumeDto(
    val id: String,
    val filePath: String? = null,
    val parseStatus: String? = null,
    val originalName: String? = null
)

data class ChangePasswordRequest(val currentPassword: String, val newPassword: String)

data class QuickApplyRequest(
    @SerializedName("resume_id") val resumeId: String,
    @SerializedName("cover_letter") val coverLetter: String = "",
    val answers: Map<String, String>? = null
)

data class QuickApplyResponse(
    @SerializedName("application_id") val applicationId: String? = null,
    val id: String? = null,
    val status: String? = null
) {
    fun resolvedId(): String = applicationId ?: id.orEmpty()
}
