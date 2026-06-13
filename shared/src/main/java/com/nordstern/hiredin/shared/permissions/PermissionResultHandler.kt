package com.nordstern.hiredin.shared.permissions

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionResultHandler @Inject constructor(
    private val permissionManager: PermissionManager
) {
    fun handleResult(results: Map<String, Boolean>): PermissionResult {
        val granted = results.filterValues { it }.keys
        val denied = results.filterValues { !it }.keys
        return PermissionResult(granted.toList(), denied.toList())
    }

    fun allGranted(results: Map<String, Boolean>): Boolean = results.all { it.value }
}
