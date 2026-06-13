package com.nordstern.hiredin.shared.utils

import com.nordstern.hiredin.shared.utils.Logger

object CrashReporter {
    private val logger = Logger.getLogger("CrashReporter")

    fun recordException(throwable: Throwable, context: Map<String, String> = emptyMap()) {
        logger.error("Crash recorded: ${throwable.message} context=$context", throwable)
    }

    fun setUserId(userId: String?) {
        logger.info("CrashReporter userId=$userId")
    }
}
