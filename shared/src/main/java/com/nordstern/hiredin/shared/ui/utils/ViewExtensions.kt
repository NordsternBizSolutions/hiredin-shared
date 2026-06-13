package com.nordstern.hiredin.shared.ui.utils

import android.view.View

object ViewExtensions {
    fun View.visible() { visibility = View.VISIBLE }
    fun View.gone() { visibility = View.GONE }
    fun View.invisible() { visibility = View.INVISIBLE }
    fun View.enable() { isEnabled = true }
    fun View.disable() { isEnabled = false }
}
