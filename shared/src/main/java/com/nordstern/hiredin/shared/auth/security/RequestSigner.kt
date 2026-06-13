package com.nordstern.hiredin.shared.auth.security

import com.nordstern.hiredin.shared.security.EncryptionManager
import com.nordstern.hiredin.shared.security.KeyStoreManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestSigner @Inject constructor(
    private val keyStoreManager: KeyStoreManager
) {
    fun sign(payload: String, timestamp: Long): String =
        keyStoreManager.signData("$payload:$timestamp")
}
