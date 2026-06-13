package com.nordstern.hiredin.shared.security.biometric

interface BiometricCallback {
    fun onSuccess()
    fun onError(message: String)
    fun onFailed()
}
