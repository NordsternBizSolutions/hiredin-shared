package com.nordstern.hiredin.shared.api

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nordstern.hiredin.shared.BuildConfig
import com.nordstern.hiredin.shared.auth.TokenManager
import com.nordstern.hiredin.shared.build.constants.ErrorCodes
import com.nordstern.hiredin.shared.cache.DiskCache
import com.nordstern.hiredin.shared.network.NetworkManager
import com.nordstern.hiredin.shared.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseApiClient @Inject constructor(
    private val context: Context,
    private val tokenManager: TokenManager,
    private val networkManager: NetworkManager,
    private val diskCache: DiskCache
) {
    private val logger = Logger.getLogger("BaseApiClient")
    val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create()

    private var retrofit: Retrofit? = null
    private var authenticatedRetrofit: Retrofit? = null

    private val okHttpClient: OkHttpClient by lazy { buildOkHttpClient(false) }
    private val authenticatedOkHttpClient: OkHttpClient by lazy { buildOkHttpClient(true) }

    private fun buildOkHttpClient(authenticated: Boolean): OkHttpClient {
        val cacheDir = File(context.cacheDir, "http_cache")
        val cache = Cache(cacheDir, 10L * 1024 * 1024)

        val builder = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(CacheInterceptor())
            .addInterceptor(RetryInterceptor())
            .addInterceptor(LoggingInterceptor())

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor { message -> logger.debug("API: $message") }
                    .apply { level = HttpLoggingInterceptor.Level.BODY }
            )
        }

        if (authenticated) {
            builder.addInterceptor(AuthInterceptor(tokenManager))
        }

        return builder.build()
    }

    fun getAuthenticatedOkHttpClient(): OkHttpClient = authenticatedOkHttpClient

    private fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }

    private fun getAuthenticatedRetrofit(): Retrofit {
        if (authenticatedRetrofit == null) {
            authenticatedRetrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(authenticatedOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return authenticatedRetrofit!!
    }

    inline fun <reified T> createService(): T = getRetrofit().create(T::class.java)

    inline fun <reified T> createAuthenticatedService(): T = getAuthenticatedRetrofit().create(T::class.java)

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> ApiResponse<T>,
        retryCount: Int = 3,
        retryDelayMs: Long = 1000
    ): ApiResponse<T> = withContext(Dispatchers.IO) {
        var currentRetry = 0

        while (currentRetry < retryCount) {
            try {
                if (!networkManager.isNetworkAvailable()) {
                    return@withContext ApiResponse.error(
                        code = ErrorCodes.NO_INTERNET,
                        message = "No internet connection"
                    )
                }

                val response = apiCall()

                if (response.success) {
                    return@withContext response
                } else if (response.code == ErrorCodes.UNAUTHORIZED || response.code == ErrorCodes.SESSION_EXPIRED) {
                    tokenManager.clearTokens()
                    return@withContext ApiResponse.error(
                        code = ErrorCodes.SESSION_EXPIRED,
                        message = "Session expired. Please login again."
                    )
                }
                return@withContext response
            } catch (e: SocketTimeoutException) {
                logger.warn("API call timeout, retry: $currentRetry", e)
                currentRetry++
                if (currentRetry < retryCount) delay(retryDelayMs * currentRetry)
                else return@withContext ApiResponse.error(ErrorCodes.TIMEOUT, "Request timeout. Please try again.")
            } catch (e: IOException) {
                logger.warn("Network error, retry: $currentRetry", e)
                currentRetry++
                if (currentRetry < retryCount && networkManager.isNetworkAvailable()) {
                    delay(retryDelayMs * currentRetry)
                } else {
                    return@withContext ApiResponse.error(ErrorCodes.NETWORK_ERROR, "Network error. Please check your connection.")
                }
            } catch (e: HttpException) {
                logger.error("HTTP error: ${e.code()}", e)
                return@withContext ApiResponse.error(e.code().toString(), e.message() ?: "Server error occurred")
            } catch (e: Exception) {
                logger.error("Unexpected error", e)
                return@withContext ApiResponse.error(ErrorCodes.UNKNOWN, "An unexpected error occurred")
            }
        }

        ApiResponse.error(ErrorCodes.UNKNOWN, "Max retries exceeded")
    }

    suspend fun <T> executeWithOfflineSupport(
        cacheKey: String,
        apiCall: suspend () -> ApiResponse<T>,
        cacheCall: suspend () -> T? = { null },
        clazz: Class<T>
    ): Flow<ApiResponse<T>> = flow {
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
        emit(ApiResponse.error(ErrorCodes.UNKNOWN, e.message ?: "Unknown error"))
    }
}
