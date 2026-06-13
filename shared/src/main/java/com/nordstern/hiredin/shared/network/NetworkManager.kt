package com.nordstern.hiredin.shared.network

import com.nordstern.hiredin.shared.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkManager @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context,
    private val connectivityObserver: ConnectivityObserver
) {
    private val _isConnected = MutableStateFlow(connectivityObserver.isCurrentlyConnected())
    val isConnected: Flow<Boolean> = connectivityObserver.observe()

    fun isNetworkAvailable(): Boolean = connectivityObserver.isCurrentlyConnected()

    fun isWifiConnected(): Boolean = NetworkUtils.isWifiConnected(context)

    val networkQuality: kotlinx.coroutines.flow.StateFlow<NetworkQuality> = connectivityObserver.networkQuality

    fun updateConnectionState() {
        _isConnected.value = connectivityObserver.isCurrentlyConnected()
    }
}
