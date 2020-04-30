package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.entities.User
import com.google.gson.annotations.SerializedName
import java.math.BigInteger

/**
 *
 * This object contains information about an incoming pre-checkout query.
 *
 * @property [id] Unique query identifier
 * @property [from] User who sent the query
 * @property [currency] Three-letter ISO 4217 currency code
 * @property [totalAmount] Total price in the smallest units of the currency (integer, not float/double). For example, for a price of US$ 1.45 pass amount = 145.
 * @property [invoicePayload] Bot specified invoice payload
 * @property [shippingOptionId] Identifier of the shipping option chosen by the user
 * @property [orderInfo] Order info provided by the user
 * @see OrderInfo
 */
data class PreCheckoutQuery(
    val id: String,
    val from: User,
    val currency: String,
    @SerializedName("total_amount") val totalAmount: BigInteger,
    @SerializedName("invoice_payload") val invoicePayload: String,
    @SerializedName("shipping_option_id") val shippingOptionId: String?,
    @SerializedName("order_info") val orderInfo: OrderInfo?
)
