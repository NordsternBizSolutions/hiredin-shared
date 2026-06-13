package com.nordstern.hiredin.shared.permissions.permissions

import android.Manifest
import com.nordstern.hiredin.shared.permissions.PermissionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoragePermission @Inject constructor(
    private val permissionManager: PermissionManager
) {
    val permission: String
        get() = if (android.os.Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE

    fun isGranted(): Boolean = permission.isNotEmpty() && permissionManager.isGranted(permission)
}
