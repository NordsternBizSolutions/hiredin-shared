package com.nordstern.hiredin.shared.auth.security

import com.nordstern.hiredin.shared.security.EncryptionManager
import com.nordstern.hiredin.shared.security.KeyStoreManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenEncryption @Inject constructor(
    private val encryptionManager: EncryptionManager
) {
    fun encryptToken(token: String): String = encryptionManager.encrypt(token)
    fun decryptToken(encrypted: String): String = encryptionManager.decrypt(encrypted)
}
