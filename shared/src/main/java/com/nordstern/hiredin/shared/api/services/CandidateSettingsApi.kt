package com.nordstern.hiredin.shared.api.services

import com.nordstern.hiredin.shared.api.ApiResponse
import com.nordstern.hiredin.shared.build.constants.ApiEndpoints
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface CandidateSettingsApi {

    @PUT(ApiEndpoints.Candidate.SETTINGS_EMAIL)
    suspend fun updateEmail(@Body body: UpdateEmailRequest): ApiResponse<UpdateEmailResponse>

    @PUT(ApiEndpoints.Candidate.SETTINGS_PHONE)
    suspend fun updatePhone(@Body body: UpdatePhoneRequest): ApiResponse<UpdatePhoneResponse>

    @POST("candidate/settings/phone/send-otp")
    suspend fun sendPhoneOtp(@Body body: SendPhoneOtpRequest): ApiResponse<Unit>

    @POST("candidate/settings/phone/verify-otp")
    suspend fun verifyPhoneOtp(@Body body: VerifyPhoneOtpRequest): ApiResponse<Unit>

    @GET(ApiEndpoints.Candidate.SETTINGS_PRIVACY)
    suspend fun getPrivacy(): ApiResponse<PrivacyPreferencesDto>

    @PUT(ApiEndpoints.Candidate.SETTINGS_PRIVACY)
    suspend fun updatePrivacy(@Body body: PrivacyPreferencesDto): ApiResponse<PrivacyPreferencesDto>

    @GET(ApiEndpoints.Candidate.SETTINGS_APPEARANCE)
    suspend fun getAppearance(): ApiResponse<AppearancePreferencesDto>

    @PUT(ApiEndpoints.Candidate.SETTINGS_APPEARANCE)
    suspend fun updateAppearance(@Body body: AppearancePreferencesDto): ApiResponse<AppearancePreferencesDto>

    @POST(ApiEndpoints.Candidate.SETTINGS_2FA_ENABLE)
    suspend fun enable2Fa(): ApiResponse<TwoFaSetupDto>

    @POST(ApiEndpoints.Candidate.SETTINGS_2FA_VERIFY)
    suspend fun verify2Fa(@Body body: TwoFaVerifyRequest): ApiResponse<Unit>

    @POST(ApiEndpoints.Candidate.SETTINGS_2FA_DISABLE)
    suspend fun disable2Fa(@Body body: TwoFaVerifyRequest): ApiResponse<Unit>

    @DELETE("candidate/profile")
    suspend fun deleteAccount(@Query("confirm") confirm: Boolean = true): ApiResponse<Unit>
}

data class UpdateEmailRequest(val email: String)
data class UpdateEmailResponse(val requiresVerification: Boolean = false)
data class UpdatePhoneRequest(val phone: String)
data class UpdatePhoneResponse(val requiresVerification: Boolean = false)
data class SendPhoneOtpRequest(val phone: String)
data class VerifyPhoneOtpRequest(val phone: String, val code: String)

data class PrivacyPreferencesDto(
    val profileVisibility: String = "public",
    val showEmail: Boolean = true,
    val showPhone: Boolean = false,
    val showLocation: Boolean = true,
    val allowMessages: Boolean = true,
    val allowProfileViewTracking: Boolean = true
)

data class AppearancePreferencesDto(
    val theme: String = "system",
    val language: String = "en"
)

data class TwoFaSetupDto(
    val secret: String? = null,
    val qrCode: String? = null
)

data class TwoFaVerifyRequest(val token: String)
