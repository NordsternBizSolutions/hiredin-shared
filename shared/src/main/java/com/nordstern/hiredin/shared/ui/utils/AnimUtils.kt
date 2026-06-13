package com.nordstern.hiredin.shared.ui.utils

import android.view.View
import android.view.animation.AlphaAnimation

object AnimUtils {
    fun fadeIn(view: View, durationMs: Long = 300) {
        view.startAnimation(AlphaAnimation(0f, 1f).apply { duration = durationMs })
    }

    fun fadeOut(view: View, durationMs: Long = 300) {
        view.startAnimation(AlphaAnimation(1f, 0f).apply { duration = durationMs })
    }
}
