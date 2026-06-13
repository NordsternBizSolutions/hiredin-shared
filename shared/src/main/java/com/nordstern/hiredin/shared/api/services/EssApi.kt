package com.nordstern.hiredin.shared.api.services

import com.nordstern.hiredin.shared.api.ApiResponse
import com.nordstern.hiredin.shared.build.constants.ApiEndpoints
import com.nordstern.hiredin.shared.models.AttendanceRecord
import com.nordstern.hiredin.shared.models.LeaveRequest
import com.nordstern.hiredin.shared.models.Payslip
import com.nordstern.hiredin.shared.models.enums.EmployerTaskStatus
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface EssApi {

    @GET(ApiEndpoints.Ess.PROFILE)
    suspend fun getProfile(): ApiResponse<EssProfileDto>

    @PUT(ApiEndpoints.Ess.PROFILE)
    suspend fun updateProfile(@Body body: EssProfileDto): ApiResponse<EssProfileDto>

    @GET(ApiEndpoints.Ess.DASHBOARD)
    suspend fun getDashboard(): ApiResponse<EssDashboardDto>

    @GET(ApiEndpoints.Ess.LEAVE)
    suspend fun getLeaveRequests(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<LeaveRequest>>

    @POST(ApiEndpoints.Ess.LEAVE)
    suspend fun createLeaveRequest(@Body body: CreateLeaveRequest): ApiResponse<LeaveRequest>

    @POST("ess/leave/{id}/cancel")
    suspend fun cancelLeaveRequest(@Path("id") requestId: String): ApiResponse<LeaveRequest>

    @GET(ApiEndpoints.Ess.LEAVE_BALANCE)
    suspend fun getLeaveBalance(): ApiResponse<List<EssLeaveBalanceDto>>

    @GET(ApiEndpoints.Ess.LEAVE_CALENDAR)
    suspend fun getTeamLeaveCalendar(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): ApiResponse<List<TeamLeaveEntryDto>>

    @GET(ApiEndpoints.Ess.ATTENDANCE_HISTORY)
    suspend fun getAttendanceHistory(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): ApiResponse<List<AttendanceRecord>>

    @POST(ApiEndpoints.Ess.ATTENDANCE_LOG)
    suspend fun logAttendance(@Body body: AttendanceLogRequest): ApiResponse<AttendanceRecord>

    @POST(ApiEndpoints.Ess.ATTENDANCE_MISSED_PUNCH)
    suspend fun requestMissedPunch(@Body body: MissedPunchRequest): ApiResponse<Unit>

    @GET(ApiEndpoints.Ess.PAYSLIPS)
    suspend fun getPayslips(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<Payslip>>

    @GET(ApiEndpoints.Ess.PAYSLIP_DETAIL)
    suspend fun getPayslipDetail(@Path("id") payslipId: String): ApiResponse<Payslip>

    @GET(ApiEndpoints.Ess.TASKS)
    suspend fun getTasks(
        @Query("status") status: EmployerTaskStatus? = null
    ): ApiResponse<List<EssTaskDto>>

    @PUT(ApiEndpoints.Ess.TASK_DETAIL)
    suspend fun updateTask(
        @Path("id") taskId: String,
        @Body body: UpdateTaskRequest
    ): ApiResponse<EssTaskDto>

    @GET(ApiEndpoints.Ess.ANNOUNCEMENTS)
    suspend fun getAnnouncements(): ApiResponse<List<EssAnnouncementDto>>

    @GET(ApiEndpoints.Ess.TEAM_DIRECTORY)
    suspend fun getTeamDirectory(
        @Query("q") query: String? = null
    ): ApiResponse<List<TeamMemberEntryDto>>

    @GET(ApiEndpoints.Ess.ONBOARDING)
    suspend fun getOnboardingStatus(): ApiResponse<OnboardingStatusDto>
}

data class EssProfileDto(
    val employeeId: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val department: String? = null,
    val designation: String? = null,
    val email: String? = null,
    val phone: String? = null
)

data class EssDashboardDto(
    val leaveBalance: Float = 0f,
    val pendingTasks: Int = 0,
    val unreadNotifications: Int = 0,
    val nextPayDate: String? = null
)

data class CreateLeaveRequest(
    val type: String,
    val startDate: String,
    val endDate: String,
    val reason: String? = null
)

data class EssLeaveBalanceDto(val type: String, val balance: Float, val used: Float)

data class TeamLeaveEntryDto(
    val employeeName: String,
    val type: String,
    val startDate: String,
    val endDate: String
)

data class AttendanceLogRequest(
    val action: String,
    val timestamp: String? = null,
    val location: String? = null
)

data class MissedPunchRequest(
    val date: String,
    val checkIn: String? = null,
    val checkOut: String? = null,
    val reason: String
)

data class EssTaskDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val status: EmployerTaskStatus,
    val dueDate: String? = null
)

data class UpdateTaskRequest(val status: EmployerTaskStatus, val notes: String? = null)

data class EssAnnouncementDto(
    val id: String,
    val title: String,
    val body: String,
    val publishedAt: String,
    val acknowledged: Boolean = false
)

data class TeamMemberEntryDto(
    val id: String,
    val name: String,
    val department: String? = null,
    val designation: String? = null
)

data class OnboardingStatusDto(
    val completedSteps: Int,
    val totalSteps: Int,
    val checklist: List<OnboardingStepDto> = emptyList()
)

data class OnboardingStepDto(val id: String, val title: String, val completed: Boolean)
