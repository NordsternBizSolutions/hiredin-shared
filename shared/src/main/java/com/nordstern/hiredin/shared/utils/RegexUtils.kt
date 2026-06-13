package com.nordstern.hiredin.shared.utils

object RegexUtils {
    val EMAIL = Regex("""^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$""")
    val PHONE_E164 = Regex("""^\+[1-9]\d{6,14}$""")
    val PASSWORD_STRONG = Regex("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$""")
    val UUID = Regex("""^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$""")
    val URL = Regex("""^https?://[\w\-]+(\.[\w\-]+)+[/\w\- ./?%&=]*$""")

    fun matches(pattern: Regex, value: String): Boolean = pattern.matches(value)
}
