package com.nordstern.hiredin.shared.ui.utils

import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object ValidationUtils {
    fun isValidEmail(email: String): Boolean = com.nordstern.hiredin.shared.utils.RegexUtils.EMAIL.matches(email)
    fun isValidPhoneE164(phone: String): Boolean = com.nordstern.hiredin.shared.utils.RegexUtils.PHONE_E164.matches(phone)
    fun isStrongPassword(password: String): Boolean = com.nordstern.hiredin.shared.utils.RegexUtils.PASSWORD_STRONG.matches(password)
}
