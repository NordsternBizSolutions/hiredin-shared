package com.nordstern.hiredin.shared.ui.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

object DrawableUtils {
    fun getDrawable(context: Context, resId: Int): Drawable? =
        ContextCompat.getDrawable(context, resId)

    fun tint(context: Context, drawable: Drawable, colorRes: Int): Drawable {
        val wrapped = androidx.core.graphics.drawable.DrawableCompat.wrap(drawable.mutate())
        androidx.core.graphics.drawable.DrawableCompat.setTint(wrapped, ContextCompat.getColor(context, colorRes))
        return wrapped
    }
}
