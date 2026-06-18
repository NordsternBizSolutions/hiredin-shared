import java.util.Properties

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

fun hiredinLocalProperty(key: String): String? =
    localProperties.getProperty(key)?.trim()?.takeIf { it.isNotEmpty() }
        ?: System.getenv(key)?.trim()?.takeIf { it.isNotEmpty() }

fun hiredinBuildConfigString(key: String, defaultValue: String): String {
    val value = hiredinLocalProperty(key) ?: defaultValue
    return "\"$value\""
}

extra["hiredinBuildConfigString"] = ::hiredinBuildConfigString
extra["hiredinLocalProperty"] = ::hiredinLocalProperty
