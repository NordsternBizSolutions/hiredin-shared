package com.nordstern.hiredin.shared.security

import com.nordstern.hiredin.shared.auth.security.CertificatePinner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CertificateValidator @Inject constructor(private val certificatePinner: CertificatePinner) {
    fun isPinningEnabled(): Boolean = com.nordstern.hiredin.shared.build.FeatureFlags.enableCertificatePinning
    fun getPinner() = certificatePinner.build()
}
