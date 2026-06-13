package com.nordstern.hiredin.shared.workers.constraints

import androidx.work.Constraints

object StorageConstraint {
    fun notLow(): Constraints = Constraints.Builder()
        .setRequiresStorageNotLow(true)
        .build()
}
