package com.nordstern.hiredin.shared.cache.serializers

import android.util.Base64

class ProtoSerializer {
    fun serialize(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)
    fun deserialize(encoded: String): ByteArray = Base64.decode(encoded, Base64.NO_WRAP)
}
