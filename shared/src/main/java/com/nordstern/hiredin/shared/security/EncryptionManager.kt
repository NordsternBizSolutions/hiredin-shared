package com.nordstern.hiredin.shared.security

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.nordstern.hiredin.shared.utils.CryptoUtils
import com.nordstern.hiredin.shared.utils.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionManager @Inject constructor(
    context: Context
) {
    private val logger = Logger.getLogger("EncryptionManager")

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "hiredin_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val encryptionKey: ByteArray by lazy {
        val stored = encryptedPrefs.getString(KEY_ENCRYPTION_KEY, null)
        if (stored != null) {
            Base64.decode(stored, Base64.NO_WRAP)
        } else {
            val key = ByteArray(32).also { java.security.SecureRandom().nextBytes(it) }
            encryptedPrefs.edit().putString(KEY_ENCRYPTION_KEY, Base64.encodeToString(key, Base64.NO_WRAP)).apply()
            key
        }
    }

    fun encrypt(plainText: String): String = try {
        CryptoUtils.encryptAesGcm(plainText, encryptionKey)
    } catch (e: Exception) {
        logger.error("Encryption failed", e)
        plainText
    }

    fun decrypt(cipherText: String): String = try {
        CryptoUtils.decryptAesGcm(cipherText, encryptionKey)
    } catch (e: Exception) {
        logger.error("Decryption failed", e)
        cipherText
    }

    companion object {
        private const val KEY_ENCRYPTION_KEY = "encryption_key"
    }
}
