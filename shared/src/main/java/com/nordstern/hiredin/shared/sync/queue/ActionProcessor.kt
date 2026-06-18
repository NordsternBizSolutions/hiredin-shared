package com.nordstern.hiredin.shared.sync.queue

import com.nordstern.hiredin.shared.api.ApiConfig
import com.nordstern.hiredin.shared.api.BaseApiClient
import com.nordstern.hiredin.shared.database.entities.OfflineActionEntity
import com.nordstern.hiredin.shared.sync.ActionStatus
import com.nordstern.hiredin.shared.sync.OfflineActionResult
import com.nordstern.hiredin.shared.utils.Logger
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionProcessor @Inject constructor(
    private val apiClient: BaseApiClient,
    private val gson: com.google.gson.Gson
) {
    private val logger = Logger.getLogger("ActionProcessor")
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun process(action: OfflineActionEntity): OfflineActionResult {
        return try {
            val client = apiClient.getAuthenticatedOkHttpClient()
            val body = if (action.method.uppercase() in BODY_METHODS) {
                action.payload.toRequestBody(jsonMediaType)
            } else null

            val requestBuilder = Request.Builder()
                .url("${ApiConfig.getBaseUrl()}${action.endpoint}")
                .method(action.method.uppercase(), body)

            action.headers?.let { headerJson ->
                @Suppress("UNCHECKED_CAST")
                val headersMap = gson.fromJson(headerJson, Map::class.java) as Map<String, String>
                headersMap.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
            }

            val response = client.newCall(requestBuilder.build()).execute()
            if (response.isSuccessful) {
                OfflineActionResult(success = true, actionId = action.id)
            } else {
                OfflineActionResult(
                    success = false,
                    actionId = action.id,
                    error = "HTTP ${response.code}: ${response.message}"
                )
            }
        } catch (e: Exception) {
            logger.error("Action processing failed: ${action.id}", e)
            OfflineActionResult(success = false, actionId = action.id, error = e.message ?: "Unknown error")
        }
    }

    companion object {
        private val BODY_METHODS = setOf("POST", "PUT", "PATCH")
    }
}
