package com.nordstern.hiredin.shared.permissions.permissions

import android.Manifest
import com.nordstern.hiredin.shared.permissions.PermissionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarPermission @Inject constructor(
    private val permissionManager: PermissionManager
) {
    val permission: String
        get() = Manifest.permission.READ_CALENDAR

    fun isGranted(): Boolean = permission.isNotEmpty() && permissionManager.isGranted(permission)
}
