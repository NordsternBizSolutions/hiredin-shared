package com.nordstern.hiredin.shared.workers.constraints

import androidx.work.Constraints
import java.util.concurrent.TimeUnit

object TimeConstraint {
    fun withDelay(minutes: Long): Long = TimeUnit.MINUTES.toMillis(minutes)

    fun idle(): Constraints = Constraints.Builder().build()
}
