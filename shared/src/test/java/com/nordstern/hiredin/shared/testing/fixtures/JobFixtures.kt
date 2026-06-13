package com.nordstern.hiredin.shared.testing.fixtures

import com.nordstern.hiredin.shared.models.User
import com.nordstern.hiredin.shared.models.enums.UserRole
import com.nordstern.hiredin.shared.models.enums.UserStatus

object JobFixtures {
    fun publishedJob() = com.nordstern.hiredin.shared.models.Job(
        id = "job_test_001",
        title = "Senior Android Developer",
        description = "Build mobile apps",
        status = com.nordstern.hiredin.shared.models.enums.JobStatus.PUBLISHED
    )
}
