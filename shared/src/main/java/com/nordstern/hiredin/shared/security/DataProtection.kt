package com.nordstern.hiredin.shared.security

import com.nordstern.hiredin.shared.auth.security.CertificatePinner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataProtection @Inject constructor(private val encryptionManager: EncryptionManager) {
    fun protectField(value: String): String = encryptionManager.encrypt(value)
    fun revealField(encrypted: String): String = encryptionManager.decrypt(encrypted)
    fun maskEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return "***"
        return "${parts[0].take(2)}***@${parts[1]}"
    }
}
