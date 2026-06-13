package com.nordstern.hiredin.shared.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.nordstern.hiredin.shared.utils.CryptoUtils
import com.nordstern.hiredin.shared.utils.Logger
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyStoreManager @Inject constructor() {
    private val logger = Logger.getLogger("KeyStoreManager")
    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    fun getOrCreateSecretKey(alias: String = DEFAULT_ALIAS): SecretKey {
        val existing = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
        if (existing != null) return existing.secretKey

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        logger.info("Created new KeyStore secret key: $alias")
        return keyGenerator.generateKey()
    }

    fun signData(data: String, alias: String = SIGNING_ALIAS): String {
        return CryptoUtils.sha256("$alias:$data")
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val DEFAULT_ALIAS = "hiredin_aes_key"
        private const val SIGNING_ALIAS = "hiredin_signing_key"
    }
}
