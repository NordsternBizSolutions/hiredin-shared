package com.nordstern.hiredin.shared.di

import com.nordstern.hiredin.shared.analytics.providers.AnalyticsProvider
import com.nordstern.hiredin.shared.analytics.providers.CustomAnalyticsProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @IntoSet
    @Singleton
    fun provideCustomAnalyticsProvider(provider: CustomAnalyticsProvider): AnalyticsProvider = provider
}
