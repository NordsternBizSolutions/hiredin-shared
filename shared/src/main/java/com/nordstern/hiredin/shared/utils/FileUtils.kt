package com.nordstern.hiredin.shared.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    fun readText(context: Context, relativePath: String): String? =
        runCatching { context.assets.open(relativePath).bufferedReader().use { it.readText() } }.getOrNull()

    fun writeText(file: File, content: String) {
        file.parentFile?.mkdirs()
        file.writeText(content)
    }

    fun copyStream(input: java.io.InputStream, output: FileOutputStream) {
        input.use { inp -> output.use { out -> inp.copyTo(out) } }
    }

    fun deleteRecursive(file: File): Boolean = file.deleteRecursively()

    fun sizeMb(file: File): Double = file.length() / (1024.0 * 1024.0)
}
