package com.nordstern.hiredin.shared.testing

import com.nordstern.hiredin.shared.models.User
import com.nordstern.hiredin.shared.models.enums.UserRole
import com.nordstern.hiredin.shared.models.enums.UserStatus
import java.util.UUID

object TestDataFactory {
    fun user(
        id: String = UUID.randomUUID().toString(),
        email: String = "test@example.com",
        role: UserRole = UserRole.CANDIDATE
    ) = User(
        id = id,
        email = email,
        role = role,
        status = UserStatus.ACTIVE,
        firstName = "Test",
        lastName = "User"
    )

    fun randomId(): String = UUID.randomUUID().toString()
}
