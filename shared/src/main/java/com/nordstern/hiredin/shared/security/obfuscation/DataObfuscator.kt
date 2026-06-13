package com.nordstern.hiredin.shared.security.obfuscation

import android.util.Base64

object DataObfuscator {
    fun obfuscate(value: String): String =
        Base64.encodeToString(value.reversed().toByteArray(), Base64.NO_WRAP)

    fun deobfuscate(value: String): String =
        String(Base64.decode(value, Base64.NO_WRAP)).reversed()
}
