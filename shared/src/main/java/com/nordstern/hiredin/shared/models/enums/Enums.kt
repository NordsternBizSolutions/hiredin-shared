package com.nordstern.hiredin.shared.models.enums

enum class UserRole {
    CANDIDATE, EMPLOYEE, EMPLOYER, RECRUITER, HR_MANAGER, HR_DIRECTOR
}

enum class UserStatus {
    ACTIVE, SUSPENDED, PENDING_VERIFICATION
}

enum class JobStatus {
    DRAFT, PUBLISHED, CLOSED
}

enum class EmploymentType {
    FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, TEMPORARY
}

enum class ApplicationStatus {
    APPLIED, VIEWED, IN_REVIEW, SHORTLISTED, INTERVIEW_SCHEDULED,
    REJECTED, OFFERED, HIRED, WITHDRAWN
}

enum class CandidateVisaStatus {
    CITIZEN, RESIDENT, WORK_VISA, VISIT_VISA, NO_VISA
}

enum class EmployeeStatus {
    ONBOARDING, ACTIVE, ON_LEAVE, SUSPENDED, TERMINATED, INACTIVE
}

enum class LeaveType {
    ANNUAL, SICK, PERSONAL, MATERNITY, PATERNITY, UNPAID, COMPASSIONATE
}

enum class LeaveStatus {
    PENDING, APPROVED, REJECTED, CANCELLED, HR_REVIEW
}

enum class AttendanceStatus {
    PRESENT, ABSENT, HALF_DAY, REMOTE, LATE
}

enum class AttendanceSource {
    EMPLOYEE_PORTAL, BIOMETRIC, API
}

enum class EmployerTaskStatus {
    NOT_STARTED, STARTED, IN_PROGRESS, COMPLETED, CANCELLED
}

enum class OfflineActionStatus {
    PENDING, SYNCED, FAILED
}
