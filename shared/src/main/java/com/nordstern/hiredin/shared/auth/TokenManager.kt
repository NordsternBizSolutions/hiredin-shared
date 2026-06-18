package com.nordstern.hiredin.shared.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nordstern.hiredin.shared.api.AuthApi
import com.nordstern.hiredin.shared.api.BaseApiClient
import com.nordstern.hiredin.shared.api.RefreshRequest
import com.nordstern.hiredin.shared.build.constants.SharedConstants
import com.nordstern.hiredin.shared.security.EncryptionManager
import com.nordstern.hiredin.shared.utils.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

data class AuthState(
    val accessToken: String?,
    val refreshToken: String?,
    val tokenExpiry: Long?,
    val isAuthenticated: Boolean
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

@Singleton
class TokenManager @Inject constructor(
    private val context: Context,
    private val encryptionManager: EncryptionManager,
    private val apiClientProvider: Provider<BaseApiClient>
) {
    private val logger = Logger.getLogger("TokenManager")

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val TOKEN_EXPIRY_KEY = stringPreferencesKey("token_expiry")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val DEVICE_ID_KEY = stringPreferencesKey("device_id")
    }

    private fun safeDecrypt(value: String?): String? = try {
        value?.let { encryptionManager.decrypt(it) }
    } catch (_: com.nordstern.hiredin.shared.security.EncryptionException) {
        null
    }

    private val authFlow: Flow<AuthState> = context.authDataStore.data.map { preferences ->
        val accessToken = safeDecrypt(preferences[ACCESS_TOKEN_KEY])
        val refreshToken = safeDecrypt(preferences[REFRESH_TOKEN_KEY])
        val expiry = preferences[TOKEN_EXPIRY_KEY]?.toLongOrNull()
        AuthState(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenExpiry = expiry,
            isAuthenticated = accessToken != null && expiry?.let { System.currentTimeMillis() < it } == true
        )
    }

    suspend fun getAccessToken(): String? = authFlow.first().accessToken

    suspend fun getRefreshToken(): String? = authFlow.first().refreshToken

    suspend fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long) {
        val expiry = System.currentTimeMillis() + (expiresIn * 1000)
        context.authDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = encryptionManager.encrypt(accessToken)
            preferences[REFRESH_TOKEN_KEY] = encryptionManager.encrypt(refreshToken)
            preferences[TOKEN_EXPIRY_KEY] = expiry.toString()
        }
        logger.info("Tokens saved successfully")
    }

    suspend fun clearTokens() {
        context.authDataStore.edit { it.clear() }
        logger.info("Tokens cleared")
    }

    suspend fun isTokenValid(): Boolean {
        val state = authFlow.first()
        return state.isAuthenticated && state.tokenExpiry?.let {
            System.currentTimeMillis() < it - SharedConstants.TOKEN_REFRESH_BUFFER_MS
        } == true
    }

    suspend fun refreshTokenIfNeeded(): Boolean {
        if (isTokenValid()) return true
        val refreshToken = getRefreshToken() ?: return false
        return try {
            val newTokens = refreshAccessToken(refreshToken)
            saveTokens(newTokens.accessToken, newTokens.refreshToken, newTokens.expiresIn)
            true
        } catch (e: Exception) {
            logger.error("Token refresh failed", e)
            clearTokens()
            false
        }
    }

    private suspend fun refreshAccessToken(refreshToken: String): TokenResponse {
        val apiClient = apiClientProvider.get()
        val response = apiClient.safeApiCall {
            apiClient.createService<AuthApi>().refresh(
                RefreshRequest(refreshToken = refreshToken, deviceId = getDeviceId())
            )
        }
        if (response.success && response.data != null) {
            val data = response.data
            return TokenResponse(
                accessToken = data.resolvedAccessToken(),
                refreshToken = data.refreshToken,
                expiresIn = data.resolvedExpiresInSeconds()
            )
        }
        throw IllegalStateException(response.error ?: "Token refresh failed")
    }

    suspend fun saveDeviceId(deviceId: String) {
        context.authDataStore.edit { preferences ->
            preferences[DEVICE_ID_KEY] = encryptionManager.encrypt(deviceId)
        }
    }

    suspend fun getDeviceId(): String? =
        safeDecrypt(context.authDataStore.data.first()[DEVICE_ID_KEY])

    suspend fun saveUserId(userId: String) {
        context.authDataStore.edit { preferences ->
            preferences[USER_ID_KEY] = encryptionManager.encrypt(userId)
        }
    }

    suspend fun getUserId(): String? =
        safeDecrypt(context.authDataStore.data.first()[USER_ID_KEY])

    fun observeAuthState(): Flow<AuthState> = authFlow
}
