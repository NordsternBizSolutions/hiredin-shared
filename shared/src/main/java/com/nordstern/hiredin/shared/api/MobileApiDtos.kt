package com.nordstern.hiredin.shared.api

import com.nordstern.hiredin.shared.models.Application
import com.nordstern.hiredin.shared.models.CandidateProfile
import com.nordstern.hiredin.shared.models.Conversation
import com.google.gson.JsonObject
import com.nordstern.hiredin.shared.models.Job
import com.nordstern.hiredin.shared.models.Message
import com.nordstern.hiredin.shared.models.enums.CandidateVisaStatus
import com.nordstern.hiredin.shared.models.enums.EmploymentType
import com.nordstern.hiredin.shared.models.enums.JobStatus
import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.OffsetDateTime

fun parseIsoMillis(value: String?): Long {
    if (value.isNullOrBlank()) return 0L
    return runCatching { Instant.parse(value).toEpochMilli() }
        .getOrElse {
            runCatching { OffsetDateTime.parse(value).toInstant().toEpochMilli() }
                .getOrDefault(0L)
        }
}

/** GET candidate/profile — nested profile + stats. */
data class CandidateProfileResponseDto(
    val profile: CandidateProfilePayloadDto? = null,
    val user: ProfileUserDto? = null,
    val stats: ProfileStatsDto? = null,
    val completeness: SmartCvCompletenessDto? = null,
    val subscription: ProfileSubscriptionDto? = null,
    @SerializedName("openToWork", alternate = ["open_to_work", "isOpenToWork", "is_open_to_work"])
    val openToWork: Boolean? = null
)

data class ProfileUserDto(
    val email: String? = null,
    val phone: String? = null,
    val phoneVerified: Boolean = false,
    val memberSince: String? = null,
    val twoFactorEnabled: Boolean = false,
    val status: String? = null
)

data class ProfileSubscriptionDto(
    val plan: String? = null,
    val status: String? = null,
    val endDate: String? = null
)

data class EmploymentContractDto(
    val id: String,
    val applicationId: String? = null,
    val status: String? = null,
    val jobTitle: String? = null,
    val companyName: String? = null,
    val companyLogo: String? = null,
    val pdfUrl: String? = null,
    val signedPdfUrl: String? = null,
    val signedAt: String? = null,
    val createdAt: String? = null
)

data class ContractsListDto(val contracts: List<EmploymentContractDto> = emptyList())

data class ProfileStatsDto(
    val applicationsCount: Int = 0,
    val interviewsCount: Int = 0,
    val offersCount: Int = 0,
    @SerializedName("profileCompletion", alternate = ["profileCompleteness", "profile_completion"])
    val profileCompletion: Int = 0,
    @SerializedName("profileViews", alternate = ["profile_views", "profile_views_count", "total_profile_views"])
    val profileViews: Int = 0,
    @SerializedName("savedJobsCount", alternate = ["saved_jobs_count"])
    val savedJobsCount: Int = 0
)

data class SmartCvCompletenessDto(
    val overall: Int = 0,
    val percent: Int? = null,
    val score: Int? = null
)

data class DashboardCountDto(val total: Int = 0)

data class DashboardOffersDto(
    val pending: Int = 0,
    val accepted: Int = 0,
    val rejected: Int = 0
)

data class DashboardBasicStatsDto(
    val applications: DashboardCountDto? = null,
    val interviews: DashboardCountDto? = null,
    val offers: DashboardOffersDto? = null
)

data class DashboardRecentActivityDto(
    val id: String,
    val type: String? = null,
    @SerializedName("activityType") val activityType: String? = null,
    val description: String? = null,
    val jobTitle: String? = null,
    val companyName: String? = null,
    val createdAt: String? = null
)

data class DashboardStatsDto(
    val applicationsCount: Int = 0,
    val interviewsCount: Int = 0,
    val offersCount: Int = 0,
    val profileCompletion: Int = 0,
    val basic: DashboardBasicStatsDto? = null,
    @SerializedName("profile_completion") val profileCompletionAlt: Int? = null,
    @SerializedName("profile_completion_remaining") val profileCompletionRemaining: Int = 0,
    @SerializedName("job_matches_total") val jobMatchesTotal: Int = 0,
    @SerializedName("job_matches_new_today") val jobMatchesNewToday: Int = 0,
    @SerializedName("active_applications") val activeApplications: Int = 0,
    @SerializedName("interviews_scheduled") val interviewsScheduled: Int = 0,
    @SerializedName("applications_this_week") val applicationsThisWeek: Int = 0,
    @SerializedName("profile_views_this_week", alternate = ["profileViews", "profile_views", "total_profile_views", "profileViewsCount"])
    val profileViewsThisWeek: Int = 0,
    @SerializedName("saved_jobs_total") val savedJobsTotal: Int = 0,
    @SerializedName("recentActivity") val recentActivity: List<DashboardRecentActivityDto>? = null
) {
    fun resolvedProfileCompletion(): Int =
        profileCompletion.takeIf { it > 0 }
            ?: profileCompletionAlt
            ?: 0
}

data class ApplicationJobRefDto(
    val id: String? = null
)

data class ApplicationListItemDto(
    val id: String,
    @SerializedName("job_id", alternate = ["jobId"])
    val jobId: String? = null,
    val job: ApplicationJobRefDto? = null,
    val jobTitle: String? = null,
    val companyName: String? = null,
    @SerializedName("company_id", alternate = ["companyId"])
    val companyId: String? = null,
    @SerializedName("company_logo", alternate = ["companyLogo", "logo_url", "logoUrl", "logo"])
    val companyLogo: String? = null,
    val company: CompanyBriefDto? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class CandidateProfilePayloadDto(
    val firstName: String? = null,
    val lastName: String? = null,
    val headline: String? = null,
    val location: String? = null,
    val yearsExp: Int? = null,
    val expectedSalary: Int? = null,
    val currentSalary: Int? = null,
    val visaStatus: String? = null,
    @SerializedName("openToWork", alternate = ["open_to_work", "isOpenToWork", "is_open_to_work"])
    val openToWork: Boolean = false,
    val isProfileComplete: Boolean = false,
    val avatarUrl: String? = null
)

data class JobListItemDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val company: CompanyBriefDto? = null,
    val location: String? = null,
    val city: String? = null,
    val country: String? = null,
    @SerializedName("country_code", alternate = ["countryCode"]) val countryCode: String? = null,
    val salaryMin: Double? = null,
    val salaryMax: Double? = null,
    @SerializedName("currency", alternate = ["salaryCurrency"])
    val salaryCurrency: String? = "AED",
    @SerializedName("employment", alternate = ["type"])
    val type: String? = null,
    val employerUserId: String? = null,
    val employerUser: EmployerUserJobDto? = null,
    val hasApplied: Boolean? = null,
    val status: String? = null,
    val createdAt: String? = null,
    @SerializedName("posted_at", alternate = ["publishedAt", "published_at"])
    val postedAt: String? = null,
    @SerializedName("application_count", alternate = ["applicationsCount", "applicantCount", "applicantsCount"])
    val applicationCount: Int? = null,
    val userStatus: JobUserStatusDto? = null,
    val isSaved: Boolean = false,
    @SerializedName("company_name") val companyName: String? = null,
    @SerializedName("company_logo", alternate = ["companyLogo"])
    val companyLogo: String? = null
)

data class JobUserStatusDto(
    val isSaved: Boolean = false,
    val hasApplied: Boolean = false
)

data class CompanyBriefDto(
    val id: String? = null,
    val name: String? = null,
    @SerializedName("logo", alternate = ["logoUrl", "logo_url", "company_logo", "companyLogo"])
    val logoUrl: String? = null
)

data class EmployerUserJobDto(
    val id: String? = null,
    val employerProfile: EmployerProfileJobDto? = null
)

data class EmployerProfileJobDto(
    val id: String? = null,
    val companyName: String? = null,
    val logo: String? = null,
    val location: String? = null,
    val industry: String? = null
)

data class ConversationListItemDto(
    val id: String,
    val otherParty: ConversationPartyDto? = null,
    val participantName: String? = null,
    val participantUserId: String? = null,
    val lastMessage: ConversationLastMessageDto? = null,
    val unreadCount: Int = 0,
    val updatedAt: String? = null,
    val jobTitle: String? = null,
    val jobId: String? = null
)

data class ConversationPartyDto(
    val id: String,
    val name: String,
    val avatar: String? = null,
    @SerializedName("logo", alternate = ["logoUrl"])
    val logoUrl: String? = null
)

data class ConversationLastMessageDto(
    val id: String? = null,
    val content: String? = null,
    val createdAt: String? = null
)

data class MessageListItemDto(
    val id: String,
    val senderId: String,
    @SerializedName("body", alternate = ["content"])
    val body: String? = null,
    val createdAt: String? = null,
    val isMine: Boolean = false
)

object MobileApiMappers {

    fun mapProfile(userId: String, dto: CandidateProfileResponseDto): CandidateProfile? {
        val p = dto.profile ?: return null
        return CandidateProfile(
            userId = userId,
            firstName = p.firstName,
            lastName = p.lastName,
            headline = p.headline,
            location = p.location,
            yearsExp = p.yearsExp,
            expectedSalary = p.expectedSalary,
            currentSalary = p.currentSalary,
            visaStatus = p.visaStatus?.let { 
                try { CandidateVisaStatus.valueOf(it.uppercase()) } catch (_: Exception) { null }
            },
            openToWork = dto.openToWork ?: p.openToWork,
            isProfileComplete = p.isProfileComplete,
            avatarUrl = p.avatarUrl
        )
    }

    fun profileCompleteness(dto: CandidateProfileResponseDto): Int =
        dto.stats?.profileCompletion
            ?: dto.completeness?.overall
            ?: dto.completeness?.percent
            ?: dto.completeness?.score
            ?: if (dto.profile?.isProfileComplete == true) 100 else 0

    fun isPremium(dto: CandidateProfileResponseDto): Boolean {
        val sub = dto.subscription ?: return false
        val plan = sub.plan ?: return false
        return plan != "FREE" && sub.status != "CANCELLED"
    }

    fun mapJob(dto: JobListItemDto): Job {
        val employerProfile = dto.employerUser?.employerProfile
        val locationMeta = JsonObject().apply {
            dto.city?.takeIf { it.isNotBlank() }?.let { addProperty("city", it) }
                ?: dto.location?.trim()?.takeIf { it.isNotBlank() }?.let { addProperty("city", it) }
            dto.country?.takeIf { it.isNotBlank() }?.let { addProperty("country", it) }
            dto.countryCode?.takeIf { it.isNotBlank() }?.let { addProperty("countryCode", it) }
            employerProfile?.companyName?.takeIf { it.isNotBlank() }?.let { addProperty("companyName", it) }
            employerProfile?.logo?.takeIf { it.isNotBlank() }?.let { addProperty("companyLogo", it) }
        }
        val metadata = if (locationMeta.size() > 0) locationMeta else null
        val resolvedCompanyId = dto.company?.id ?: dto.employerUser?.id ?: dto.employerUserId
        val resolvedCompanyName = dto.company?.name?.takeIf { it.isNotBlank() }
            ?: dto.companyName?.takeIf { it.isNotBlank() }
            ?: employerProfile?.companyName?.takeIf { it.isNotBlank() }
        val resolvedCompanyLogo = dto.company?.logoUrl?.takeIf { it.isNotBlank() }
            ?: dto.companyLogo?.takeIf { it.isNotBlank() }
            ?: employerProfile?.logo?.takeIf { it.isNotBlank() }
        return Job(
        id = dto.id,
        companyId = resolvedCompanyId,
        companyName = resolvedCompanyName,
        companyLogo = resolvedCompanyLogo,
        title = dto.title,
        location = dto.location,
        salaryMin = dto.salaryMin,
        salaryMax = dto.salaryMax,
        currency = dto.salaryCurrency ?: "AED",
        description = dto.description.orEmpty(),
        employerUserId = dto.employerUserId,
        employment = dto.type?.let { raw ->
            val normalized = raw.uppercase().replace("-", "_").replace(" ", "_")
            try { EmploymentType.valueOf(normalized) } catch (_: Exception) { null }
        },
        status = dto.status?.let { try { JobStatus.valueOf(it.uppercase()) } catch (_: Exception) { JobStatus.PUBLISHED } } ?: JobStatus.PUBLISHED,
        publishedAt = (dto.postedAt ?: dto.createdAt)?.let { java.util.Date(parseIsoMillis(it)) },
        applicationCount = dto.applicationCount,
        isSaved = dto.userStatus?.isSaved ?: dto.isSaved,
        hasApplied = dto.hasApplied == true || dto.userStatus?.hasApplied == true,
        postingMetadata = metadata
    )
    }

    fun mapApplication(dto: ApplicationListItemDto, userId: String): Application = Application(
        id = dto.id,
        jobId = dto.jobId?.takeIf { it.isNotBlank() } ?: dto.job?.id.orEmpty(),
        candidateUserId = userId,
        status = dto.status?.let { try { com.nordstern.hiredin.shared.models.enums.ApplicationStatus.valueOf(it.uppercase()) } catch (_: Exception) { com.nordstern.hiredin.shared.models.enums.ApplicationStatus.APPLIED } } ?: com.nordstern.hiredin.shared.models.enums.ApplicationStatus.APPLIED,
        createdAt = java.util.Date(parseIsoMillis(dto.createdAt ?: dto.updatedAt)),
        updatedAt = java.util.Date(parseIsoMillis(dto.updatedAt))
    )

    fun mapDashboardStats(dto: DashboardStatsDto, profileCompleteness: Int): com.nordstern.hiredin.shared.api.services.CandidateDashboardDto {
        val applications = dto.applicationsCount.takeIf { it > 0 }
            ?: dto.basic?.applications?.total
            ?: 0
        val interviews = dto.interviewsCount.takeIf { it > 0 }
            ?: dto.basic?.interviews?.total
            ?: 0
        val offers = dto.offersCount.takeIf { it > 0 }
            ?: dto.basic?.offers?.pending
            ?: 0
        return com.nordstern.hiredin.shared.api.services.CandidateDashboardDto(
            applicationsCount = applications,
            interviewsCount = interviews,
            offersCount = offers,
            profileCompleteness = profileCompleteness.takeIf { it > 0 } ?: dto.profileCompletion
        )
    }

    fun mapConversation(dto: ConversationListItemDto): Conversation =
        Conversation(
            id = dto.id,
            participantName = dto.otherParty?.name ?: dto.participantName ?: dto.jobTitle ?: "Conversation",
            participantUserId = dto.otherParty?.id ?: dto.participantUserId,
            participantAvatarUrl = dto.otherParty?.avatar ?: dto.otherParty?.logoUrl,
            lastMessage = dto.lastMessage?.content,
            unreadCount = dto.unreadCount,
            updatedAt = parseIsoMillis(dto.updatedAt ?: dto.lastMessage?.createdAt)
        )

    fun mapMessage(dto: MessageListItemDto): Message =
        Message(
            id = dto.id,
            senderId = dto.senderId,
            body = dto.body.orEmpty(),
            createdAt = parseIsoMillis(dto.createdAt),
            isMine = dto.isMine
        )
}
