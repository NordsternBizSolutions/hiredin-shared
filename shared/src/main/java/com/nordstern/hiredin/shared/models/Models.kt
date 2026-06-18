package com.nordstern.hiredin.shared.models

import com.google.gson.JsonObject
import com.nordstern.hiredin.shared.models.enums.ApplicationStatus
import com.nordstern.hiredin.shared.models.enums.AttendanceSource
import com.nordstern.hiredin.shared.models.enums.AttendanceStatus
import com.nordstern.hiredin.shared.models.enums.CandidateVisaStatus
import com.nordstern.hiredin.shared.models.enums.EmployeeStatus
import com.nordstern.hiredin.shared.models.enums.EmploymentType
import com.nordstern.hiredin.shared.models.enums.JobStatus
import com.nordstern.hiredin.shared.models.enums.LeaveStatus
import com.nordstern.hiredin.shared.models.enums.LeaveType
import com.nordstern.hiredin.shared.models.enums.UserRole
import com.nordstern.hiredin.shared.models.enums.UserStatus
import java.util.Date

data class User(
    val id: String,
    val email: String,
    val role: UserRole,
    val status: UserStatus,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneE164: String? = null,
    val lastLoginAt: Date? = null
)

data class CandidateProfile(
    val userId: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val headline: String? = null,
    val location: String? = null,
    val yearsExp: Int? = null,
    val expectedSalary: Int? = null,
    val currentSalary: Int? = null,
    val visaStatus: CandidateVisaStatus? = null,
    val openToWork: Boolean = false,
    val isProfileComplete: Boolean = false,
    val avatarUrl: String? = null
)

data class Application(
    val id: String,
    val jobId: String,
    val candidateUserId: String,
    val status: ApplicationStatus,
    val coverLetter: String? = null,
    val statusHistory: JsonObject? = null,
    val createdAt: Date,
    val updatedAt: Date
)

data class Employee(
    val id: String,
    val userId: String,
    val companyId: String,
    val employeeCode: String,
    val department: String? = null,
    val designation: String? = null,
    val status: EmployeeStatus,
    val joiningDate: Date? = null
)

data class LeaveRequest(
    val id: String,
    val employeeId: String,
    val type: LeaveType,
    val startDate: Date,
    val endDate: Date,
    val days: Float? = null,
    val reason: String? = null,
    val status: LeaveStatus = LeaveStatus.PENDING,
    val approvedById: String? = null
)

data class AttendanceRecord(
    val id: String,
    val employeeId: String,
    val date: Date,
    val checkIn: Date? = null,
    val checkOut: Date? = null,
    val status: AttendanceStatus,
    val source: AttendanceSource = AttendanceSource.EMPLOYEE_PORTAL
)

data class Payslip(
    val id: String,
    val employeeId: String,
    val basicSalary: Float,
    val netPay: Float,
    val allowances: JsonObject? = null,
    val deductions: JsonObject? = null,
    val periodStart: Date,
    val periodEnd: Date,
    val paymentDate: Date? = null
)

data class NotificationDevice(
    val deviceId: String,
    val deviceType: String = "android",
    val pushToken: String? = null,
    val isActive: Boolean = true
)

data class StatusHistoryEntry(
    val status: String,
    val at: Date
)

data class EmployerTask(
    val id: String,
    val title: String,
    val description: String? = null,
    val assigneeId: String? = null,
    val companyId: String? = null,
    val status: com.nordstern.hiredin.shared.models.enums.EmployerTaskStatus,
    val dueDate: Date? = null,
    val createdAt: Date,
    val updatedAt: Date
)

data class PlatformNotification(
    val id: String,
    val userId: String,
    val title: String,
    val body: String,
    val type: String,
    val read: Boolean = false,
    val deepLink: String? = null,
    val createdAt: Date,
    val updatedAt: Date
)
