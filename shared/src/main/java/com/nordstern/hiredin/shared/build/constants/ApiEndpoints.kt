package com.nordstern.hiredin.shared.build.constants

object ApiEndpoints {
    object Candidate {
        const val PROFILE = "candidate/profile"
        const val WORK_EXPERIENCE = "candidate/work-experience"
        const val EDUCATION = "candidate/education"
        const val SKILLS = "candidate/skills"
        const val LANGUAGES = "candidate/languages"
        const val CERTIFICATIONS = "candidate/certifications"
        const val SOCIAL_LINKS = "candidate/social-links"
        const val CAREER_HUB = "candidate/career-hub"
        const val APPLICATIONS_TRACKER = "candidate/applications-tracker"
        const val SAVED_JOBS = "candidate/saved-jobs"
        const val DASHBOARD = "candidate/dashboard"
        const val CONVERSATIONS = "candidate/conversations"
        const val INTERVIEWS = "candidate/interviews"
        const val OFFERS = "candidate/offers"
        const val AVATAR = "candidate/profile/avatar"
        const val RESUME = "candidate/resume/upload"
        const val SETTINGS = "candidate/settings"
        const val SETTINGS_PASSWORD = "candidate/settings/password"
    }

    object Jobs {
        const val SEARCH = "jobs/search"
        const val DETAIL = "jobs/{id}"
        const val APPLY = "jobs/{id}/apply"
        const val SAVE = "jobs/{id}/save"
        const val UNSAVE = "jobs/{id}/unsave"
        const val RECOMMENDED = "jobs/recommended"
        const val SIMILAR = "jobs/{id}/similar"
        const val FILTERS = "jobs/filters"
        const val JOB_ALERTS = "jobs/job-alerts"
    }

    object Notifications {
        const val LIST = "notifications"
        const val DETAIL = "notifications/{id}"
        const val SETTINGS = "notifications/settings"
    }

    object Entity {
        const val COMPANY = "entity/company"
        const val COMPANY_LOGO = "entity/company/logo"
        const val JOBS = "entity/jobs"
        const val JOB_SUBMIT = "entity/jobs/{id}/submit"
        const val JOB_PUBLISH = "entity/jobs/{id}/publish"
        const val APPROVAL_QUEUE = "entity/jobs/approval-queue"
        const val APPLICATIONS = "entity/applications"
        const val INTERVIEWS = "entity/interviews"
        const val OFFERS = "entity/offers"
        const val TEAM = "entity/team"
        const val COMPLIANCE = "entity/compliance"
        const val TASKS = "entity/tasks"
    }

    object Hrms {
        const val SESSION = "hrms/session"
        const val DASHBOARD = "hrms/dashboard"
        const val NAV_COUNTS = "hrms/nav-counts"
        const val EMPLOYEES = "hrms/employees"
        const val EMPLOYEE_DETAIL = "hrms/employees/{id}"
        const val EMPLOYEE_RESET_PASSWORD = "hrms/employees/{id}/reset-password"
        const val TIME_OFF_REQUESTS = "hrms/time-off/requests"
        const val TIME_OFF_BALANCES = "hrms/time-off/balances"
        const val ATTENDANCE = "hrms/attendance/summary"
        const val PAYROLL_RUNS = "hrms/payroll/runs"
        const val PAYROLL_ITEMS = "hrms/payroll/items"
        const val PAYROLL_EXPORT = "hrms/payroll/export"
        const val COMPLIANCE_RENEWALS = "hrms/compliance/renewals"
        const val ANNOUNCEMENTS = "hrms/announcements"
    }

    object Ess {
        const val PROFILE = "ess/profile"
        const val DASHBOARD = "ess/dashboard"
        const val LEAVE = "ess/leave"
        const val LEAVE_BALANCE = "ess/leave/balance"
        const val LEAVE_CALENDAR = "ess/leave/team-calendar"
        const val ATTENDANCE_HISTORY = "ess/attendance/history"
        const val ATTENDANCE_LOG = "ess/attendance/log"
        const val ATTENDANCE_MISSED_PUNCH = "ess/attendance/missed-punch"
        const val PAYSLIPS = "ess/payslips"
        const val PAYSLIP_DETAIL = "ess/payslips/{id}"
        const val TASKS = "ess/tasks"
        const val TASK_DETAIL = "ess/tasks/{id}"
        const val ANNOUNCEMENTS = "ess/announcements"
        const val TEAM_DIRECTORY = "ess/team/directory"
        const val ONBOARDING = "ess/onboarding/status"
    }
}
