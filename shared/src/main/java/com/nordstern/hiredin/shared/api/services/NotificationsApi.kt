package com.nordstern.hiredin.shared.api.services

import com.nordstern.hiredin.shared.api.ApiResponse
import com.nordstern.hiredin.shared.build.constants.ApiEndpoints
import com.nordstern.hiredin.shared.notifications.models.Notification
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationsApi {

    @GET(ApiEndpoints.Notifications.LIST)
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("unreadOnly") unreadOnly: Boolean = false
    ): ApiResponse<List<Notification>>

    @GET(ApiEndpoints.Notifications.DETAIL)
    suspend fun getNotification(@Path("id") notificationId: String): ApiResponse<Notification>

    @POST("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") notificationId: String): ApiResponse<Unit>

    @POST("notifications/read-all")
    suspend fun markAllAsRead(): ApiResponse<Unit>

    @GET(ApiEndpoints.Notifications.SETTINGS)
    suspend fun getSettings(): ApiResponse<NotificationSettingsDto>

    @PUT(ApiEndpoints.Notifications.SETTINGS)
    suspend fun updateSettings(@Body body: NotificationSettingsDto): ApiResponse<NotificationSettingsDto>
}

data class NotificationSettingsDto(
    val pushEnabled: Boolean = true,
    val emailEnabled: Boolean = true,
    val leaveAlerts: Boolean = true,
    val payrollAlerts: Boolean = true,
    val taskAlerts: Boolean = true,
    val announcementAlerts: Boolean = true
)
