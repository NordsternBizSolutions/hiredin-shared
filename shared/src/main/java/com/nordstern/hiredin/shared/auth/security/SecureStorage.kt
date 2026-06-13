package com.nordstern.hiredin.shared.auth.security

import javax.inject.Inject
import javax.inject.Singleton

/** Auth-layer facade delegating to [com.nordstern.hiredin.shared.security.SecureStorage]. */
@Singleton
class SecureStorage @Inject constructor(
    private val delegate: com.nordstern.hiredin.shared.security.SecureStorage
) {
    fun putString(key: String, value: String) = delegate.putString(key, value)
    fun getString(key: String): String? = delegate.getString(key)
    fun putBoolean(key: String, value: Boolean) = delegate.putBoolean(key, value)
    fun getBoolean(key: String, default: Boolean = false): Boolean = delegate.getBoolean(key, default)
    fun remove(key: String) = delegate.remove(key)
    fun clear() = delegate.clear()
}
