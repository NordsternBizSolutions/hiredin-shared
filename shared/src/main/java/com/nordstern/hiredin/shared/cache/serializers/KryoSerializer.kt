package com.nordstern.hiredin.shared.cache.serializers

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import android.util.Base64

class KryoSerializer {
    fun <T> serialize(obj: T): String {
        val bytes = ByteArrayOutputStream().use { bos ->
            ObjectOutputStream(bos).use { it.writeObject(obj) }
            bos.toByteArray()
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> deserialize(encoded: String): T {
        val bytes = Base64.decode(encoded, Base64.NO_WRAP)
        return ByteArrayInputStream(bytes).use { bis ->
            ObjectInputStream(bis).use { it.readObject() as T }
        }
    }
}
