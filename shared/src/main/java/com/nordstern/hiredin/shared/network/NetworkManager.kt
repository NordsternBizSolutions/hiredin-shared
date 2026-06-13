package com.nordstern.hiredin.shared.network

import android.content.Context
import com.nordstern.hiredin.shared.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkManager @Inject constructor(
    private val context: Context
) {
    private val _isConnected = MutableStateFlow(isNetworkAvailable())
    val isConnected: Flow<Boolean> = _isConnected.asStateFlow()

    fun isNetworkAvailable(): Boolean = NetworkUtils.isNetworkAvailable(context)

    fun isWifiConnected(): Boolean = NetworkUtils.isWifiConnected(context)

    fun updateConnectionState() {
        _isConnected.value = isNetworkAvailable()
    }
}
