package me.ivmg.telegram.entities.payment

import com.google.gson.annotations.SerializedName as Name

data class SuccessfulPayment(
    val currency: String,
    @Name("total_amount") val totalAmount: Int,
    @Name("invoice_payload") val invoicePayload: String,
    @Name("shipping_option_id") val shippingOptionId: String?,
    @Name("order_info") val orderInfo: OrderInfo?,
    @Name("telegram_payment_charge_id") val telegramPaymentChargeId: String,
    @Name("provider_payment_charge_id") val providerPaymentChargeId: String
)