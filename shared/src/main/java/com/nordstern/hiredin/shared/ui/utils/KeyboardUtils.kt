package com.nordstern.hiredin.shared.ui.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import android.content.Context

object KeyboardUtils {
    fun hide(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        activity.currentFocus?.let { imm.hideSoftInputFromWindow(it.windowToken, 0) }
    }

    fun show(view: android.view.View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}
