package com.nordstern.hiredin.shared.utils

object ForegroundChecker {
    fun isAppInForeground(): Boolean = AppLifecycleManager.isInForeground.value

    fun requireForeground(block: () -> Unit) {
        if (isAppInForeground()) block()
    }
}
