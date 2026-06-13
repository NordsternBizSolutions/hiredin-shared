package com.nordstern.hiredin.shared.testing.fixtures

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
