package com.nordstern.hiredin.shared.di

import com.nordstern.hiredin.shared.notifications.handlers.ApprovalNotificationHandler
import com.nordstern.hiredin.shared.notifications.handlers.AnnouncementNotificationHandler
import com.nordstern.hiredin.shared.notifications.handlers.ComplianceNotificationHandler
import com.nordstern.hiredin.shared.notifications.handlers.LeaveNotificationHandler
import com.nordstern.hiredin.shared.notifications.handlers.NotificationTypeHandler
import com.nordstern.hiredin.shared.notifications.handlers.PayrollNotificationHandler
import com.nordstern.hiredin.shared.notifications.handlers.TaskNotificationHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides @IntoSet @Singleton
    fun provideLeaveHandler(h: LeaveNotificationHandler): NotificationTypeHandler = h

    @Provides @IntoSet @Singleton
    fun providePayrollHandler(h: PayrollNotificationHandler): NotificationTypeHandler = h

    @Provides @IntoSet @Singleton
    fun provideAnnouncementHandler(h: AnnouncementNotificationHandler): NotificationTypeHandler = h

    @Provides @IntoSet @Singleton
    fun provideTaskHandler(h: TaskNotificationHandler): NotificationTypeHandler = h

    @Provides @IntoSet @Singleton
    fun provideApprovalHandler(h: ApprovalNotificationHandler): NotificationTypeHandler = h

    @Provides @IntoSet @Singleton
    fun provideComplianceHandler(h: ComplianceNotificationHandler): NotificationTypeHandler = h
}
