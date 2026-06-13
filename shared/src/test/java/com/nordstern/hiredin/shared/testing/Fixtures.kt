package com.nordstern.hiredin.shared.testing

import com.nordstern.hiredin.shared.models.User
import com.nordstern.hiredin.shared.models.enums.UserRole
import com.nordstern.hiredin.shared.models.enums.UserStatus

object UserFixtures {
    fun activeUser() = User(
        id = "user_test_001",
        email = "candidate@example.com",
        role = UserRole.CANDIDATE,
        status = UserStatus.ACTIVE,
        firstName = "Test",
        lastName = "User"
    )
}

object JobFixtures {
    fun publishedJob() = com.nordstern.hiredin.shared.models.Job(
        id = "job_test_001",
        title = "Senior Android Developer",
        description = "Build mobile apps",
        status = com.nordstern.hiredin.shared.models.enums.JobStatus.PUBLISHED
    )
}
