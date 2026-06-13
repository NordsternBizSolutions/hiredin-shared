package com.nordstern.hiredin.shared.security.obfuscation

object LogObfuscator {
    private val emailRegex = Regex("[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+")
    private val phoneRegex = Regex("""\+?[0-9]{7,15}""")

    fun sanitize(message: String): String =
        message.replace(emailRegex, "[EMAIL]")
            .replace(phoneRegex, "[PHONE]")
}
