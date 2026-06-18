package com.nordstern.hiredin.shared.api

import com.google.gson.annotations.SerializedName

data class SubscriptionPlansResponseDto(
    val current: CurrentSubscriptionDto? = null,
    val plans: List<SubscriptionPlanDto>? = null,
    // Legacy flat subscription payload (before current/plans envelope)
    val id: String? = null,
    @SerializedName("planId", alternate = ["plan_id"])
    val planId: String? = null,
    @SerializedName("subsPlan", alternate = ["subs_plan"])
    val subsPlan: String? = null,
    val status: String? = null,
    @SerializedName("isPremium", alternate = ["is_premium"])
    val isPremium: Boolean? = null,
    val amount: Double? = null,
    val currency: String? = null,
    @SerializedName("currentPeriodEnd", alternate = ["current_period_end"])
    val currentPeriodEnd: String? = null,
    @SerializedName("endDate", alternate = ["end_date"])
    val endDate: String? = null,
    @SerializedName("autoRenew", alternate = ["auto_renew"])
    val autoRenew: Boolean? = null
) {
    fun resolvedCurrent(): CurrentSubscriptionDto? =
        current ?: planId?.let {
            CurrentSubscriptionDto(
                planCode = it,
                planName = null,
                status = status,
                billingCycle = null,
                endDate = endDate ?: currentPeriodEnd,
                cancelAtPeriodEnd = autoRenew == false,
                subscriptionId = id
            )
        } ?: subsPlan?.let {
            CurrentSubscriptionDto(
                planCode = it,
                planName = null,
                status = status,
                billingCycle = null,
                endDate = endDate ?: currentPeriodEnd,
                cancelAtPeriodEnd = autoRenew == false,
                subscriptionId = id
            )
        }

    fun resolvedPlans(): List<SubscriptionPlanDto> = plans.orEmpty()
}

data class CurrentSubscriptionDto(
    @SerializedName("planCode", alternate = ["plan_code", "planId", "plan_id", "subsPlan", "subs_plan"])
    val planCode: String? = null,
    val planName: String? = null,
    val status: String? = null,
    @SerializedName("billingCycle", alternate = ["billing_cycle"])
    val billingCycle: String? = null,
    @SerializedName("endDate", alternate = ["end_date", "currentPeriodEnd"])
    val endDate: String? = null,
    @SerializedName("cancelAtPeriodEnd", alternate = ["cancel_at_period_end"])
    val cancelAtPeriodEnd: Boolean = false,
    @SerializedName("subscriptionId", alternate = ["subscription_id"])
    val subscriptionId: String? = null
)

data class SubscriptionPlanDto(
    val id: String? = null,
    @SerializedName("code", alternate = ["planId", "plan_id"])
    val code: String? = null,
    val name: String? = null,
    val tagline: String? = null,
    val description: String? = null,
    @SerializedName("priceMonthly", alternate = ["price_monthly", "monthlyPrice"])
    val priceMonthly: Double? = null,
    @SerializedName("priceYearly", alternate = ["price_yearly", "yearlyPrice"])
    val priceYearly: Double? = null,
    val currency: String? = "AED",
    val features: List<String> = emptyList(),
    @SerializedName("isPopular", alternate = ["is_popular", "popular"])
    val isPopular: Boolean = false,
    @SerializedName("sortOrder", alternate = ["sort_order"])
    val sortOrder: Int = 0,
    @SerializedName("stripePriceIdMonthly", alternate = ["stripe_price_id_monthly"])
    val stripePriceIdMonthly: String? = null,
    @SerializedName("stripePriceIdYearly", alternate = ["stripe_price_id_yearly"])
    val stripePriceIdYearly: String? = null
)

data class SubscriptionCheckoutRequestDto(
    @SerializedName("planId", alternate = ["plan_id", "planCode", "plan_code"])
    val planId: String,
    @SerializedName("billingCycle", alternate = ["billing_cycle"])
    val billingCycle: String,
    val provider: String,
    val flow: String? = null,
    @SerializedName("paymentMethodId", alternate = ["payment_method_id"])
    val paymentMethodId: String? = null
)

data class SubscriptionCheckoutResponseDto(
    @SerializedName("sessionId", alternate = ["session_id", "checkoutSessionId", "checkout_session_id"])
    val sessionId: String? = null,
    @SerializedName("clientSecret", alternate = ["client_secret"])
    val clientSecret: String? = null,
    @SerializedName("publishableKey", alternate = ["publishable_key"])
    val publishableKey: String? = null,
    @SerializedName("checkoutUrl", alternate = ["checkout_url", "redirectUrl", "redirect_url"])
    val checkoutUrl: String? = null,
    @SerializedName("paymentIntentId", alternate = ["payment_intent_id"])
    val paymentIntentId: String? = null,
    @SerializedName("subscriptionId", alternate = ["subscription_id"])
    val subscriptionId: String? = null,
    @SerializedName("orderId", alternate = ["order_id", "paypalOrderId", "paypal_order_id"])
    val orderId: String? = null
)

data class SubscriptionSetupIntentResponseDto(
    @SerializedName("clientSecret", alternate = ["client_secret"])
    val clientSecret: String? = null,
    @SerializedName("publishableKey", alternate = ["publishable_key"])
    val publishableKey: String? = null,
    @SerializedName("setupIntentId", alternate = ["setup_intent_id"])
    val setupIntentId: String? = null
)

data class SubscriptionPaymentMethodDto(
    val id: String,
    val brand: String? = null,
    @SerializedName("last4")
    val last4: String? = null,
    @SerializedName("expMonth", alternate = ["exp_month"])
    val expMonth: Int? = null,
    @SerializedName("expYear", alternate = ["exp_year"])
    val expYear: Int? = null,
    @SerializedName("isDefault", alternate = ["is_default"])
    val isDefault: Boolean = false
)

data class SubscriptionConfirmRequestDto(
    @SerializedName("planId", alternate = ["plan_id", "planCode", "plan_code"])
    val planId: String,
    @SerializedName("subscriptionId", alternate = ["subscription_id"])
    val subscriptionId: String? = null,
    @SerializedName("paymentIntentId", alternate = ["payment_intent_id"])
    val paymentIntentId: String? = null,
    @SerializedName("setupIntentId", alternate = ["setup_intent_id"])
    val setupIntentId: String? = null,
    @SerializedName("checkoutSessionId", alternate = ["checkout_session_id", "sessionId", "session_id"])
    val checkoutSessionId: String? = null,
    @SerializedName("paypalOrderId", alternate = ["paypal_order_id", "orderId", "order_id"])
    val paypalOrderId: String? = null
)
