package com.nordstern.hiredin.shared.api

import android.content.Context
import com.google.gson.Gson
import com.nordstern.hiredin.shared.BuildConfig
import com.nordstern.hiredin.shared.api.metrics.ApiMetricsCollector
import com.nordstern.hiredin.shared.auth.TokenManager
import com.nordstern.hiredin.shared.build.constants.ErrorCodes
import com.nordstern.hiredin.shared.cache.DiskCache
import com.nordstern.hiredin.shared.di.AuthenticatedClient
import com.nordstern.hiredin.shared.di.UnauthenticatedClient
import com.nordstern.hiredin.shared.network.NetworkManager
import com.nordstern.hiredin.shared.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseApiClient @Inject constructor(
    private val context: Context,
    private val gson: Gson,
    private val tokenManager: TokenManager,
    private val networkManager: NetworkManager,
    private val diskCache: DiskCache,
    private val metricsCollector: ApiMetricsCollector,
    @UnauthenticatedClient private val okHttpClient: OkHttpClient,
    @AuthenticatedClient private val authenticatedOkHttpClient: OkHttpClient
) {
    private val logger = Logger.getLogger("BaseApiClient")

    @Volatile private var configuredBaseUrl: String? = null
    @Volatile private var unauthenticatedRetrofit: Retrofit? = null
    @Volatile private var authenticatedRetrofitInstance: Retrofit? = null

    val retrofit: Retrofit
        get() = retrofitFor(unauthenticated = true)

    val authenticatedRetrofit: Retrofit
        get() = retrofitFor(unauthenticated = false)

    @Synchronized
    private fun retrofitFor(unauthenticated: Boolean): Retrofit {
        val baseUrl = ApiConfig.getBaseUrl()
        if (configuredBaseUrl != baseUrl) {
            configuredBaseUrl = baseUrl
            unauthenticatedRetrofit = null
            authenticatedRetrofitInstance = null
        }
        return if (unauthenticated) {
            unauthenticatedRetrofit ?: buildRetrofit(okHttpClient, baseUrl).also {
                unauthenticatedRetrofit = it
            }
        } else {
            authenticatedRetrofitInstance ?: buildRetrofit(authenticatedOkHttpClient, baseUrl).also {
                authenticatedRetrofitInstance = it
            }
        }
    }

    private fun buildRetrofit(client: OkHttpClient, baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    fun getAuthenticatedOkHttpClient(): OkHttpClient = authenticatedOkHttpClient

    inline fun <reified T> createService(): T = retrofit.create(T::class.java)

    inline fun <reified T> createAuthenticatedService(): T = authenticatedRetrofit.create(T::class.java)

    suspend fun <T> safeApiCall(
        retryCount: Int = 3,
        retryDelayMs: Long = 1000,
        apiCall: suspend () -> ApiResponse<T>
    ): ApiResponse<T> = withContext(Dispatchers.IO) {
        var currentRetry = 0
        val startTime = System.currentTimeMillis()

        while (currentRetry < retryCount) {
            try {
                if (!networkManager.isNetworkAvailable()) {
                    metricsCollector.recordFailure("no_network", System.currentTimeMillis() - startTime)
                    return@withContext ApiResponse.error(ErrorCodes.NO_INTERNET, "No internet connection")
                }

                val response = apiCall()
                val duration = System.currentTimeMillis() - startTime

                if (response.success) {
                    metricsCollector.recordSuccess(duration)
                    return@withContext response
                }

                if (response.code == ErrorCodes.UNAUTHORIZED || response.code == ErrorCodes.SESSION_EXPIRED) {
                    tokenManager.clearTokens()
                    metricsCollector.recordFailure(response.code ?: "auth", duration)
                    return@withContext ApiResponse.error(ErrorCodes.SESSION_EXPIRED, "Session expired. Please login again.")
                }

                metricsCollector.recordFailure(response.code ?: "api_error", duration)
                return@withContext response
            } catch (e: SocketTimeoutException) {
                logger.warn("API call timeout, retry: $currentRetry", e)
                currentRetry++
                if (currentRetry < retryCount) delay(retryDelayMs * currentRetry)
                else {
                    metricsCollector.recordFailure(ErrorCodes.TIMEOUT, System.currentTimeMillis() - startTime)
                    return@withContext ApiResponse.error(ErrorCodes.TIMEOUT, "Request timeout. Please try again.")
                }
            } catch (e: IOException) {
                logger.warn("Network error, retry: $currentRetry", e)
                currentRetry++
                if (currentRetry < retryCount && networkManager.isNetworkAvailable()) {
                    delay(retryDelayMs * currentRetry)
                } else {
                    metricsCollector.recordFailure(ErrorCodes.NETWORK_ERROR, System.currentTimeMillis() - startTime)
                    return@withContext ApiResponse.error(ErrorCodes.NETWORK_ERROR, "Network error. Please check your connection.")
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.use { it.string() }
                val serverMessage = ResponseParser.parseErrorMessage(errorBody)
                logger.error("HTTP error: ${e.code()} body=$errorBody", e)
                metricsCollector.recordFailure(e.code().toString(), System.currentTimeMillis() - startTime)
                return@withContext ApiResponse.error(
                    e.code().toString(),
                    serverMessage ?: e.message() ?: "Server error occurred"
                )
            } catch (e: Exception) {
                logger.error("Unexpected error", e)
                metricsCollector.recordFailure(ErrorCodes.UNKNOWN, System.currentTimeMillis() - startTime)
                return@withContext ApiResponse.error(ErrorCodes.UNKNOWN, "An unexpected error occurred")
            }
        }

        ApiResponse.error(ErrorCodes.UNKNOWN, "Max retries exceeded")
    }

    suspend fun <T> executeWithOfflineSupport(
        cacheKey: String,
        apiCall: suspend () -> ApiResponse<T>,
        cacheCall: suspend () -> T? = { null }
    ): Flow<ApiResponse<out T>> = flow {
        val cachedData = cacheCall()
        if (cachedData != null) {
            emit(ApiResponse.success(cachedData, isFromCache = true))
        }

        if (networkManager.isNetworkAvailable()) {
            val response = safeApiCall { apiCall() }
            if (response.success && response.data != null) {
                diskCache.put(cacheKey, gson.toJson(response.data))
                emit(response.copy(isFromCache = false))
            } else if (cachedData == null) {
                emit(response)
            }
        } else if (cachedData == null) {
            emit(ApiResponse.error(ErrorCodes.NO_INTERNET, "No internet connection and no cached data available"))
        }
    }.catch { e ->
        logger.error("Flow error", e)
        emit(ApiResponse.error<T>(ErrorCodes.UNKNOWN, e.message ?: "Unknown error"))
    }
}
