package com.nordstern.hiredin.shared.security.biometric

import android.content.Context
import androidx.biometric.BiometricManager as AndroidBiometricManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricManager @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    fun canAuthenticate(): Boolean {
        val manager = AndroidBiometricManager.from(context)
        return manager.canAuthenticate(
            AndroidBiometricManager.Authenticators.BIOMETRIC_STRONG or
                AndroidBiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == AndroidBiometricManager.BIOMETRIC_SUCCESS
    }

    fun statusMessage(): String {
        val manager = AndroidBiometricManager.from(context)
        return when (manager.canAuthenticate(AndroidBiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            AndroidBiometricManager.BIOMETRIC_SUCCESS -> "Available"
            AndroidBiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "No biometrics enrolled"
            AndroidBiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "No biometric hardware"
            else -> "Unavailable"
        }
    }
}
