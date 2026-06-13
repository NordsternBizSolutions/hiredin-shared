package com.nordstern.hiredin.shared.api.services

import com.google.gson.JsonObject
import com.nordstern.hiredin.shared.api.ApiResponse
import com.nordstern.hiredin.shared.build.constants.ApiEndpoints
import com.nordstern.hiredin.shared.models.AttendanceRecord
import com.nordstern.hiredin.shared.models.Employee
import com.nordstern.hiredin.shared.models.LeaveRequest
import com.nordstern.hiredin.shared.models.Payslip
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface HrmsApi {

    @GET(ApiEndpoints.Hrms.SESSION)
    suspend fun getSession(): ApiResponse<HrmsSessionDto>

    @GET(ApiEndpoints.Hrms.DASHBOARD)
    suspend fun getDashboard(): ApiResponse<HrmsDashboardDto>

    @GET(ApiEndpoints.Hrms.NAV_COUNTS)
    suspend fun getNavCounts(): ApiResponse<HrmsNavCountsDto>

    @GET(ApiEndpoints.Hrms.EMPLOYEES)
    suspend fun getEmployees(
        @Query("department") department: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<Employee>>

    @GET(ApiEndpoints.Hrms.EMPLOYEE_DETAIL)
    suspend fun getEmployee(@Path("id") employeeId: String): ApiResponse<Employee>

    @POST(ApiEndpoints.Hrms.EMPLOYEES)
    suspend fun createEmployee(@Body body: CreateEmployeeRequest): ApiResponse<Employee>

    @PUT(ApiEndpoints.Hrms.EMPLOYEE_DETAIL)
    suspend fun updateEmployee(
        @Path("id") employeeId: String,
        @Body body: UpdateEmployeeRequest
    ): ApiResponse<Employee>

    @POST(ApiEndpoints.Hrms.EMPLOYEE_RESET_PASSWORD)
    suspend fun resetEmployeePassword(@Path("id") employeeId: String): ApiResponse<Unit>

    @GET(ApiEndpoints.Hrms.TIME_OFF_REQUESTS)
    suspend fun getTimeOffRequests(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<LeaveRequest>>

    @POST("hrms/time-off/requests/{id}/approve")
    suspend fun approveTimeOff(@Path("id") requestId: String): ApiResponse<LeaveRequest>

    @POST("hrms/time-off/requests/{id}/reject")
    suspend fun rejectTimeOff(
        @Path("id") requestId: String,
        @Body body: RejectRequest
    ): ApiResponse<LeaveRequest>

    @GET(ApiEndpoints.Hrms.TIME_OFF_BALANCES)
    suspend fun getTimeOffBalances(@Query("employeeId") employeeId: String? = null): ApiResponse<List<LeaveBalanceDto>>

    @GET(ApiEndpoints.Hrms.ATTENDANCE)
    suspend fun getAttendance(
        @Query("employeeId") employeeId: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): ApiResponse<List<AttendanceRecord>>

    @GET(ApiEndpoints.Hrms.PAYROLL_RUNS)
    suspend fun getPayrollRuns(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<PayrollRunDto>>

    @GET(ApiEndpoints.Hrms.PAYROLL_ITEMS)
    suspend fun getPayrollItems(
        @Query("runId") runId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<Payslip>>

    @POST(ApiEndpoints.Hrms.PAYROLL_EXPORT)
    suspend fun exportPayroll(@Body body: PayrollExportRequest): ApiResponse<PayrollExportResponse>

    @GET(ApiEndpoints.Hrms.COMPLIANCE_RENEWALS)
    suspend fun getComplianceRenewals(): ApiResponse<List<ComplianceRenewalDto>>

    @GET(ApiEndpoints.Hrms.ANNOUNCEMENTS)
    suspend fun getAnnouncements(): ApiResponse<List<HrmsAnnouncementDto>>
}

data class HrmsSessionDto(val userId: String, val role: String, val companyId: String)

data class HrmsDashboardDto(
    val employeeCount: Int = 0,
    val pendingLeaveRequests: Int = 0,
    val upcomingPayroll: String? = null
)

data class HrmsNavCountsDto(
    val leave: Int = 0,
    val attendance: Int = 0,
    val payroll: Int = 0,
    val compliance: Int = 0
)

data class CreateEmployeeRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val department: String? = null,
    val designation: String? = null,
    val employeeCode: String
)

data class UpdateEmployeeRequest(
    val department: String? = null,
    val designation: String? = null,
    val status: String? = null
)

data class RejectRequest(val reason: String)

data class LeaveBalanceDto(
    val employeeId: String,
    val type: String,
    val balance: Float,
    val used: Float
)

data class PayrollRunDto(
    val id: String,
    val periodStart: String,
    val periodEnd: String,
    val status: String,
    val paymentDate: String? = null
)

data class PayrollExportRequest(val runId: String, val format: String)
data class PayrollExportResponse(val downloadUrl: String)

data class ComplianceRenewalDto(
    val id: String,
    val employeeName: String,
    val documentType: String,
    val expiryDate: String
)

data class HrmsAnnouncementDto(
    val id: String,
    val title: String,
    val body: String,
    val publishedAt: String
)
