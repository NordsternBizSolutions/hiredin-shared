package com.nordstern.hiredin.shared.ui.utils

import androidx.compose.ui.graphics.Color

object ColorUtils {
    fun fromHex(hex: String): Color {
        val cleaned = hex.removePrefix("#")
        return Color(android.graphics.Color.parseColor("#$cleaned"))
    }

    fun withAlpha(color: Color, alpha: Float): Color = color.copy(alpha = alpha)
}
