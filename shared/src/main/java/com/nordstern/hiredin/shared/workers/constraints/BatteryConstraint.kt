package com.nordstern.hiredin.shared.workers.constraints

import androidx.work.Constraints

object BatteryConstraint {
    fun notLow(): Constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()

    fun charging(): Constraints = Constraints.Builder()
        .setRequiresCharging(true)
        .build()
}
