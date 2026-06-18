package com.nordstern.hiredin.shared.api.services

import com.nordstern.hiredin.shared.api.ApiResponse
import com.nordstern.hiredin.shared.api.CurrentSubscriptionDto
import com.nordstern.hiredin.shared.api.SubscriptionCheckoutRequestDto
import com.nordstern.hiredin.shared.api.SubscriptionCheckoutResponseDto
import com.nordstern.hiredin.shared.api.SubscriptionConfirmRequestDto
import com.nordstern.hiredin.shared.api.SubscriptionPaymentMethodDto
import com.nordstern.hiredin.shared.api.SubscriptionPlansResponseDto
import com.nordstern.hiredin.shared.api.SubscriptionSetupIntentResponseDto
import com.nordstern.hiredin.shared.build.constants.ApiEndpoints
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SubscriptionApi {

    @GET(ApiEndpoints.Candidate.SUBSCRIPTION)
    suspend fun getSubscription(): ApiResponse<SubscriptionPlansResponseDto>

    @GET(ApiEndpoints.Candidate.SUBSCRIPTION_PAYMENT_METHODS)
    suspend fun getPaymentMethods(): ApiResponse<List<SubscriptionPaymentMethodDto>>

    @POST(ApiEndpoints.Candidate.SUBSCRIPTION_CHECKOUT)
    suspend fun createCheckout(@Body body: SubscriptionCheckoutRequestDto): ApiResponse<SubscriptionCheckoutResponseDto>

    @POST(ApiEndpoints.Candidate.SUBSCRIPTION_SETUP_INTENT)
    suspend fun createSetupIntent(): ApiResponse<SubscriptionSetupIntentResponseDto>

    @POST(ApiEndpoints.Candidate.SUBSCRIPTION_CONFIRM)
    suspend fun confirmSubscription(@Body body: SubscriptionConfirmRequestDto): ApiResponse<CurrentSubscriptionDto>

    @POST(ApiEndpoints.Candidate.SUBSCRIPTION_CANCEL)
    suspend fun cancelSubscription(): ApiResponse<Unit>
}
