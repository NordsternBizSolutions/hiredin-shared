package com.nordstern.hiredin.shared.security

import android.util.Base64
import com.nordstern.hiredin.shared.utils.CryptoUtils
import com.nordstern.hiredin.shared.utils.Logger
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

class EncryptionException(message: String, cause: Throwable? = null) : Exception(message, cause)

@Singleton
class EncryptionManager @Inject constructor(
    private val secureStorage: SecureStorage,
    private val keyStoreManager: KeyStoreManager
) {
    private val logger = Logger.getLogger("EncryptionManager")

    private val encryptionKey: ByteArray by lazy {
        val stored = secureStorage.getString(KEY_ENCRYPTION_KEY)
        if (stored != null) {
            Base64.decode(stored, Base64.NO_WRAP)
        } else {
            val key = ByteArray(32).also { SecureRandom().nextBytes(it) }
            secureStorage.putString(KEY_ENCRYPTION_KEY, Base64.encodeToString(key, Base64.NO_WRAP))
            key
        }
    }

    fun encrypt(plainText: String): String = try {
        CryptoUtils.encryptAesGcm(plainText, encryptionKey)
    } catch (e: Exception) {
        logger.error("Encryption failed", e)
        throw EncryptionException("Failed to encrypt data", e)
    }

    fun decrypt(cipherText: String): String = try {
        CryptoUtils.decryptAesGcm(cipherText, encryptionKey)
    } catch (e: Exception) {
        logger.error("Decryption failed", e)
        throw EncryptionException("Failed to decrypt data", e)
    }

    fun hash(value: String): String = CryptoUtils.sha256(value)

    companion object {
        private const val KEY_ENCRYPTION_KEY = "encryption_key"
    }
}
