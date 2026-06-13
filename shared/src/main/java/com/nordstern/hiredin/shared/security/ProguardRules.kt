package com.nordstern.hiredin.shared.security

object ProguardRules {
    val KEEP_RULES = listOf(
        "-keep class com.nordstern.hiredin.shared.models.** { *; }",
        "-keep class com.nordstern.hiredin.shared.api.** { *; }",
        "-keepclassmembers class * { @com.google.gson.annotations.SerializedName <fields>; }",
        "-keep @androidx.room.Entity class *",
        "-keepclassmembers class * extends androidx.work.Worker { public <init>(...); }"
    )

    fun asProguardFileContent(): String = KEEP_RULES.joinToString("\n")
}
