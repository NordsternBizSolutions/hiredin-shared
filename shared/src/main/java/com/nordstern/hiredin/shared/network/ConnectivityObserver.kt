package com.nordstern.hiredin.shared.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.nordstern.hiredin.shared.utils.Logger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityObserver @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    private val logger = Logger.getLogger("ConnectivityObserver")
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkQuality = MutableStateFlow(evaluateQuality())
    val networkQuality: StateFlow<NetworkQuality> = _networkQuality.asStateFlow()

    fun observe(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _networkQuality.value = evaluateQuality()
                trySend(true)
            }

            override fun onLost(network: Network) {
                _networkQuality.value = NetworkQuality.NONE
                trySend(false)
            }

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                _networkQuality.value = evaluateQuality(capabilities)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)
        trySend(isCurrentlyConnected())

        awaitClose {
            runCatching { connectivityManager.unregisterNetworkCallback(callback) }
                .onFailure { logger.warn("Failed to unregister network callback", it) }
        }
    }.distinctUntilChanged()

    fun isCurrentlyConnected(): Boolean = evaluateQuality() != NetworkQuality.NONE

    private fun evaluateQuality(capabilities: NetworkCapabilities? = null): NetworkQuality {
        val activeNetwork = connectivityManager.activeNetwork
        val caps = capabilities
            ?: activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
            ?: return NetworkQuality.NONE

        return when {
            !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> NetworkQuality.NONE
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                val downKbps = caps.linkDownstreamBandwidthKbps
                when {
                    downKbps >= 10_000 -> NetworkQuality.EXCELLENT
                    downKbps >= 5_000 -> NetworkQuality.GOOD
                    else -> NetworkQuality.MODERATE
                }
            }
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                if (caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)) {
                    NetworkQuality.GOOD
                } else {
                    NetworkQuality.MODERATE
                }
            }
            else -> NetworkQuality.POOR
        }
    }
}
