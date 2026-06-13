package com.nordstern.hiredin.shared.api

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
    val code: String? = null,
    val meta: ResponseMeta? = null,
    val isFromCache: Boolean = false
) {
    companion object {
        fun <T> success(data: T, meta: ResponseMeta? = null, isFromCache: Boolean = false): ApiResponse<T> =
            ApiResponse(success = true, data = data, meta = meta, isFromCache = isFromCache)

        fun <T> error(code: String, message: String): ApiResponse<T> =
            ApiResponse(success = false, error = message, code = code)
    }
}

data class ResponseMeta(
    val page: Int? = null,
    val limit: Int? = null,
    val total: Int? = null,
    @SerializedName("hasMore") val hasMore: Boolean? = null,
    @SerializedName("nextCursor") val nextCursor: String? = null
)
