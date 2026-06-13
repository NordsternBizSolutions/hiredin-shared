package com.nordstern.hiredin.shared.security

import com.nordstern.hiredin.shared.auth.security.CertificatePinner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityManager @Inject constructor(
    private val encryptionManager: EncryptionManager,
    private val keyStoreManager: KeyStoreManager,
    private val secureStorage: SecureStorage,
    private val certificatePinner: CertificatePinner
) {
    fun encrypt(plainText: String): String = encryptionManager.encrypt(plainText)
    fun decrypt(cipherText: String): String = encryptionManager.decrypt(cipherText)
    fun hash(value: String): String = encryptionManager.hash(value)
    fun clearSecureData() = secureStorage.clear()
    fun getCertificatePinner() = certificatePinner.build()
}
