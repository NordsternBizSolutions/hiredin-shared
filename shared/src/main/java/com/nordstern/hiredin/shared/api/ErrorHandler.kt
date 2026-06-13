package com.nordstern.hiredin.shared.api

import com.nordstern.hiredin.shared.build.constants.ErrorCodes
import com.nordstern.hiredin.shared.utils.Logger
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

object ErrorHandler {
    private val logger = Logger.getLogger("ErrorHandler")

    fun <T> handleException(e: Exception): ApiResponse<T> = when (e) {
        is SocketTimeoutException -> {
            logger.warn("Request timeout", e)
            ApiResponse.error(ErrorCodes.TIMEOUT, "Request timeout. Please try again.")
        }
        is IOException -> {
            logger.warn("Network error", e)
            ApiResponse.error(ErrorCodes.NETWORK_ERROR, "Network error. Please check your connection.")
        }
        is HttpException -> {
            logger.error("HTTP error: ${e.code()}", e)
            val code = when (e.code()) {
                401 -> ErrorCodes.UNAUTHORIZED
                403 -> ErrorCodes.FORBIDDEN
                404 -> ErrorCodes.NOT_FOUND
                409 -> ErrorCodes.CONFLICT
                429 -> ErrorCodes.RATE_LIMIT
                else -> ErrorCodes.INTERNAL
            }
            ApiResponse.error(code, e.message() ?: "Server error occurred")
        }
        else -> {
            logger.error("Unexpected error", e)
            ApiResponse.error(ErrorCodes.UNKNOWN, e.message ?: "An unexpected error occurred")
        }
    }
}
