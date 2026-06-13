package com.nordstern.hiredin.shared.notifications.handlers

import com.nordstern.hiredin.shared.notifications.PushNotificationHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PayrollNotificationHandler @Inject constructor(push: PushNotificationHandler) :
    BaseNotificationHandler(push, PushNotificationHandler.NOTIFICATION_TYPE_PAYROLL)
