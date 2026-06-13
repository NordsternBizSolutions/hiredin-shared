# HiredIn Shared Library ProGuard rules
-keep class com.nordstern.hiredin.shared.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-dontwarn org.eclipse.paho.**
