package com.nordstern.hiredin.shared.workers.constraints

import androidx.work.Constraints
import androidx.work.NetworkType

object NetworkConstraint {
    fun connected(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun unmetered(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .build()
}
