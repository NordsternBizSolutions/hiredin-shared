package com.nordstern.hiredin.shared.cache

import android.content.Context
import com.nordstern.hiredin.shared.utils.Logger
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiskCache @Inject constructor(context: Context) {
    private val logger = Logger.getLogger("DiskCache")
    private val cacheDir = File(context.cacheDir, "hiredin_disk_cache").also { it.mkdirs() }

    fun put(key: String, value: String) {
        try {
            File(cacheDir, sanitize(key)).writeText(value)
        } catch (e: Exception) {
            logger.error("Failed to write cache", e)
        }
    }

    fun get(key: String): String? {
        return try {
            val file = File(cacheDir, sanitize(key))
            if (file.exists()) file.readText() else null
        } catch (e: Exception) {
            logger.error("Failed to read cache", e)
            null
        }
    }

    fun remove(key: String) {
        File(cacheDir, sanitize(key)).delete()
    }

    fun clear() {
        cacheDir.listFiles()?.forEach { it.delete() }
    }

    private fun sanitize(key: String): String = key.replace(Regex("[^a-zA-Z0-9._-]"), "_")
}
