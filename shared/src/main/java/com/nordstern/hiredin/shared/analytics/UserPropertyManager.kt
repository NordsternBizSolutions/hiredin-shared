package com.nordstern.hiredin.shared.analytics

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPropertyManager @Inject constructor(private val analyticsManager: AnalyticsManager) {
    fun setRole(role: String) = analyticsManager.setUserProperty("role", role)
    fun setCompany(companyId: String) = analyticsManager.setUserProperty("company_id", companyId)
    fun setUserId(userId: String) = analyticsManager.setUserId(userId)
}
