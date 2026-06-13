package com.nordstern.hiredin.shared.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageCompressor {
    fun compress(file: File, maxWidth: Int = 1280, quality: Int = 85): File {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return file
        val ratio = minOf(1f, maxWidth.toFloat() / bitmap.width)
        val scaled = if (ratio < 1f) {
            Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt(), true)
        } else bitmap
        val out = File(file.parentFile, "${file.nameWithoutExtension}_compressed.jpg")
        FileOutputStream(out).use { fos ->
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, fos)
        }
        if (scaled != bitmap) scaled.recycle()
        bitmap.recycle()
        return out
    }

    fun toByteArray(bitmap: Bitmap, quality: Int = 85): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }
}
