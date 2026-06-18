package com.nordstern.hiredin.shared.models

import com.nordstern.hiredin.shared.models.enums.EmploymentType
import com.nordstern.hiredin.shared.models.enums.JobStatus
import com.google.gson.JsonObject
import java.util.Date

data class Job(
    val id: String,
    val companyId: String? = null,
    val employerUserId: String? = null,
    val companyName: String? = null,
    val companyLogo: String? = null,
    val title: String,
    val description: String,
    val location: String? = null,
    val salaryMin: Double? = null,
    val salaryMax: Double? = null,
    val currency: String = "AED",
    val employment: EmploymentType? = null,
    val status: JobStatus = JobStatus.PUBLISHED,
    val publishedAt: Date? = null,
    val applicationCount: Int? = null,
    val postingMetadata: JsonObject? = null,
    val deletedAt: Date? = null,
    val isSaved: Boolean = false,
    val hasApplied: Boolean = false
)
