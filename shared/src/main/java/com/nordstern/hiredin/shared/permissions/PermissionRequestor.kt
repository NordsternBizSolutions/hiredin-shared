package com.nordstern.hiredin.shared.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.nordstern.hiredin.shared.utils.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionRequestor @Inject constructor() {
    fun request(
        activity: ComponentActivity,
        permissions: Array<String>,
        onResult: (Map<String, Boolean>) -> Unit
    ) {
        val launcher = activity.activityResultRegistry.register(
            "hiredin_permission_${permissions.hashCode()}",
            ActivityResultContracts.RequestMultiplePermissions(),
            onResult
        )
        launcher.launch(permissions)
    }
}
