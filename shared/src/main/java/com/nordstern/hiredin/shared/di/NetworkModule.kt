package com.nordstern.hiredin.shared.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.nordstern.hiredin.shared.api.FlexibleInterviewListDeserializer
import com.nordstern.hiredin.shared.api.FlexibleUpcomingInterviewListDeserializer
import com.nordstern.hiredin.shared.api.UpcomingInterviewDto
import com.nordstern.hiredin.shared.api.services.InterviewDto
import com.nordstern.hiredin.shared.BuildConfig
import com.nordstern.hiredin.shared.api.MobileClientInterceptor
import com.nordstern.hiredin.shared.api.AuthInterceptor
import com.nordstern.hiredin.shared.api.CacheInterceptor
import com.nordstern.hiredin.shared.api.LoggingInterceptor
import com.nordstern.hiredin.shared.api.RetryInterceptor
import com.nordstern.hiredin.shared.auth.TokenManager
import com.nordstern.hiredin.shared.auth.security.CertificatePinner
import com.nordstern.hiredin.shared.build.constants.TimeConstants
import com.nordstern.hiredin.shared.utils.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnauthenticatedClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .serializeNulls()
        .registerTypeAdapter(
            object : TypeToken<List<InterviewDto>>() {}.type,
            FlexibleInterviewListDeserializer()
        )
        .registerTypeAdapter(
            object : TypeToken<List<UpcomingInterviewDto>>() {}.type,
            FlexibleUpcomingInterviewListDeserializer()
        )
        .create()

    @Provides
    @Singleton
    @UnauthenticatedClient
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        certificatePinner: CertificatePinner,
        tokenManager: TokenManager
    ): OkHttpClient {
        val logger = Logger.getLogger("OkHttp")
        val cacheDir = File(context.cacheDir, "http_cache")
        val cache = Cache(cacheDir, 20L * 1024 * 1024)

        val builder = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(TimeConstants.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(TimeConstants.READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(TimeConstants.WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .certificatePinner(certificatePinner.build())
            .addInterceptor(MobileClientInterceptor(tokenManager))
            .addInterceptor(CacheInterceptor())
            .addInterceptor(RetryInterceptor())
            .addInterceptor(LoggingInterceptor())

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor { message -> logger.debug(message) }
                    .apply { level = HttpLoggingInterceptor.Level.BODY }
            )
        }
        return builder.build()
    }

    @Provides
    @Singleton
    @AuthenticatedClient
    fun provideAuthenticatedOkHttpClient(
        @UnauthenticatedClient baseClient: OkHttpClient,
        tokenManager: TokenManager
    ): OkHttpClient = baseClient.newBuilder()
        .addInterceptor(AuthInterceptor(tokenManager))
        .build()
}
