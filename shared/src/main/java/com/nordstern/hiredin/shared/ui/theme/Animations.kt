package com.nordstern.hiredin.shared.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

object HiredInAnimations {
    const val SHORT_MS = 150
    const val MEDIUM_MS = 300
    const val LONG_MS = 500

    fun <T> tweenShort() = tween<T>(SHORT_MS, easing = FastOutSlowInEasing)
    fun <T> tweenMedium() = tween<T>(MEDIUM_MS, easing = FastOutSlowInEasing)
    fun <T> tweenLong() = tween<T>(LONG_MS, easing = FastOutSlowInEasing)
}
