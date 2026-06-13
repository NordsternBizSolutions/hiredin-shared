package com.nordstern.hiredin.shared.api

import android.content.Context
import com.nordstern.hiredin.shared.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class NetworkMonitor(private val context: Context) {
    fun observeNetworkStatus(): Flow<Boolean> = flow {
        emit(NetworkUtils.isNetworkAvailable(context))
    }.distinctUntilChanged()

    fun isNetworkAvailable(): Boolean = NetworkUtils.isNetworkAvailable(context)
}
