package com.nordstern.hiredin.shared.auth.security

import com.nordstern.hiredin.shared.BuildConfig
import okhttp3.CertificatePinner as OkHttpCertificatePinner
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TLS certificate pinning for production API endpoints.
 * Pins are disabled in debug builds to allow local proxying.
 */
@Singleton
class CertificatePinner @Inject constructor() {

    private val pins = mapOf(
        "your-domain.com" to listOf(
            "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=" // Replace with production pin
        )
    )

    fun build(): OkHttpCertificatePinner {
        if (BuildConfig.DEBUG) return OkHttpCertificatePinner.DEFAULT
        val builder = OkHttpCertificatePinner.Builder()
        pins.forEach { (host, sha256Pins) ->
            sha256Pins.forEach { pin -> builder.add(host, pin) }
        }
        return builder.build()
    }
}
