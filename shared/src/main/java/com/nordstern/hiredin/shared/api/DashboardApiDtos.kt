package com.nordstern.hiredin.shared.api

import com.nordstern.hiredin.shared.api.FlexibleIdDeserializer
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class JourneyOverviewDto(
    @SerializedName("total_applications") val totalApplications: Int = 0,
    @SerializedName("total_profile_views", alternate = ["totalProfileViews", "profileViews", "profile_views"])
    val totalProfileViews: Int = 0,
    @SerializedName("total_messages_received") val totalMessagesReceived: Int = 0,
    @SerializedName("total_saved_jobs") val totalSavedJobs: Int = 0,
    @SerializedName("applications_growth") val applicationsGrowth: Double = 0.0,
    @SerializedName("profile_views_growth") val profileViewsGrowth: Double = 0.0,
    @SerializedName("messages_growth") val messagesGrowth: Double = 0.0,
    @SerializedName("comparison_period") val comparisonPeriod: String? = null
)

data class ApplicationTrendDto(
    val date: String,
    @SerializedName("applications", alternate = ["applications_count", "application_count", "count"])
    val applications: Int = 0,
    @SerializedName("views", alternate = ["views_count", "profile_views"])
    val views: Int = 0,
    @SerializedName("interviews", alternate = ["interviews_count"])
    val interviews: Int = 0
)

data class JobSourceDto(
    val source: String,
    val count: Int = 0,
    val percentage: Double = 0.0,
    val color: String? = null
)

data class MatchingJobDto(
    val id: String,
    val title: String,
    val company: CompanyBriefDto? = null,
    val location: String? = null,
    @SerializedName("job_type") val jobType: String? = null,
    @SerializedName("salary_range") val salaryRange: String? = null,
    @SerializedName("salary_min") val salaryMin: Double? = null,
    @SerializedName("salary_max") val salaryMax: Double? = null,
    @SerializedName("match_score") val matchScore: Int = 0,
    @SerializedName("match_reasons") val matchReasons: List<String> = emptyList(),
    @SerializedName("posted_at") val postedAt: String? = null,
    @SerializedName("is_saved") val isSaved: Boolean = false,
    @SerializedName("has_applied") val hasApplied: Boolean = false,
    val description: String? = null,
    @SerializedName("employer_user_id") val employerUserId: String? = null
)

data class PaginatedMatchingJobsDto(
    val items: List<MatchingJobDto> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val limit: Int = 20,
    @SerializedName("has_more") val hasMore: Boolean = false,
    @SerializedName("next_cursor") val nextCursor: String? = null
)

data class UpcomingInterviewDto(
    @JsonAdapter(FlexibleIdDeserializer::class)
    @SerializedName("id", alternate = ["interviewId", "_id"]) val id: String,
    @SerializedName("job_id", alternate = ["jobId"]) val jobId: String? = null,
    @SerializedName("job_title", alternate = ["jobTitle"]) val jobTitle: String? = null,
    @SerializedName("company_name", alternate = ["companyName"]) val companyName: String? = null,
    @SerializedName("company_logo", alternate = ["companyLogo"]) val companyLogo: String? = null,
    val job: com.nordstern.hiredin.shared.api.services.InterviewJobEmbedDto? = null,
    val company: CompanyBriefDto? = null,
    @SerializedName("interview_type", alternate = ["interviewType", "type"]) val interviewType: String? = null,
    @SerializedName("interviewer_name", alternate = ["interviewerName"]) val interviewerName: String? = null,
    @SerializedName("interviewer_title", alternate = ["interviewerTitle"]) val interviewerTitle: String? = null,
    @SerializedName("duration_minutes", alternate = ["durationMinutes"]) val durationMinutes: Int = 45,
    @SerializedName("scheduled_date", alternate = ["scheduledDate"]) val scheduledDate: String? = null,
    @SerializedName("scheduled_time", alternate = ["scheduledTime"]) val scheduledTime: String? = null,
    val timezone: String? = null,
    @SerializedName("meeting_link", alternate = ["meetingLink"]) val meetingLink: String? = null,
    @SerializedName("is_virtual", alternate = ["isVirtual"]) val isVirtual: Boolean = true,
    val location: String? = null,
    val status: String? = null,
    @SerializedName("scheduled_at", alternate = ["scheduledAt"]) val scheduledAt: String? = null
) {
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
}

data class CareerVideoDto(
    val id: String,
    val title: String,
    val description: String? = null,
    @SerializedName("channel_name") val channelName: String? = null,
    @SerializedName("channel_logo") val channelLogo: String? = null,
    @SerializedName("thumbnail_url") val thumbnailUrl: String? = null,
    @SerializedName("video_url") val videoUrl: String? = null,
    val duration: String? = null,
    val views: Long = 0,
    val likes: Long = 0,
    @SerializedName("uploaded_at") val uploadedAt: String? = null,
    val category: String? = null,
    @SerializedName("is_watched") val isWatched: Boolean = false,
    @SerializedName("watch_time_seconds") val watchTimeSeconds: Int? = null
)

data class RecentActivityDto(
    val id: String,
    val type: String,
    val title: String,
    val description: String? = null,
    @SerializedName("icon_url") val iconUrl: String? = null,
    val timestamp: String? = null,
    @SerializedName("is_read") val isRead: Boolean = false,
    @SerializedName("action_url") val actionUrl: String? = null,
    val metadata: Map<String, String>? = null
)

data class CompanyListItemDto(
    @SerializedName("id", alternate = ["companyId", "company_id"])
    val id: String,
    @SerializedName("name", alternate = ["companyName", "company_name"])
    val name: String,
    @SerializedName("logo_url", alternate = ["logoUrl", "logo", "company_logo", "companyLogo"])
    val logoUrl: String? = null,
    val industry: String? = null,
    val location: String? = null,
    val city: String? = null,
    val country: String? = null,
    @SerializedName("country_code") val countryCode: String? = null,
    val description: String? = null,
    @SerializedName("follower_count") val followerCount: Int? = null,
    @SerializedName("job_count") val jobCount: Int? = null,
    val rating: Double? = null,
    @SerializedName("review_count") val reviewCount: Int? = null,
    @SerializedName("is_following") val isFollowing: Boolean = false
)

data class CompanyReviewCandidateDto(
    val id: String,
    val name: String,
    @SerializedName("avatar_url", alternate = ["avatarUrl"])
    val avatarUrl: String? = null
)

data class CompanyReviewDto(
    val id: String,
    val rating: Int,
    val feedback: String? = null,
    @SerializedName("created_at", alternate = ["createdAt"])
    val createdAt: String? = null,
    @SerializedName("updated_at", alternate = ["updatedAt"])
    val updatedAt: String? = null,
    val candidate: CompanyReviewCandidateDto? = null,
    @SerializedName("is_mine") val isMine: Boolean = false
)

data class CompanyReviewStatsDto(
    @SerializedName("average_rating", alternate = ["averageRating"])
    val averageRating: Double? = null,
    @SerializedName("review_count", alternate = ["reviewCount"])
    val reviewCount: Int = 0
)

data class CompanyReviewsResponseDto(
    val items: List<CompanyReviewDto> = emptyList(),
    val stats: CompanyReviewStatsDto? = null,
    @SerializedName("my_review") val myReview: CompanyReviewDto? = null
)

data class SubmitCompanyReviewRequest(
    val rating: Int,
    val feedback: String? = null
)

data class MarketSalaryDto(
    val average: Int = 0,
    val p10: Int = 0,
    val p25: Int = 0,
    val p50: Int = 0,
    val p75: Int = 0,
    val p90: Int = 0,
    @SerializedName("sample_size") val sampleSize: Int = 0,
    @SerializedName("location_factor") val locationFactor: Double = 1.0,
    @SerializedName("experience_factor") val experienceFactor: Double = 1.0
)

data class SalaryExpectationDto(
    @SerializedName("expected_salary") val expectedSalary: Int = 0,
    val currency: String = "AED",
    val period: String = "annual"
)

data class CandidateSkillDto(
    val name: String,
    val proficiency: Int = 3,
    @SerializedName("years_experience") val yearsExperience: Int? = null,
    val endorsements: Int = 0,
    val level: String? = null
)

data class ResumeListItemDto(
    val id: String,
    val name: String? = null,
    @SerializedName("file_url") val fileUrl: String? = null,
    @SerializedName("file_size") val fileSize: Long? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("is_default") val isDefault: Boolean = false,
    @SerializedName("original_name") val originalName: String? = null
)
