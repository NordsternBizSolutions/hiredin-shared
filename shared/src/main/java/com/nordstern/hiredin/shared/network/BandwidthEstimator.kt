package com.nordstern.hiredin.shared.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BandwidthEstimator @Inject constructor(
    private val connectivityObserver: ConnectivityObserver
) {
    fun estimate(): NetworkQuality = connectivityObserver.networkQuality.value
}
