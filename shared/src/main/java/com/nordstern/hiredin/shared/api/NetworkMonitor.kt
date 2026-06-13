package com.nordstern.hiredin.shared.api

import com.nordstern.hiredin.shared.network.ConnectivityObserver
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    private val connectivityObserver: ConnectivityObserver
) {
    fun observeNetworkStatus(): Flow<Boolean> = connectivityObserver.observe()
    fun isNetworkAvailable(): Boolean = connectivityObserver.isCurrentlyConnected()
}
