package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.entities.User
import com.google.gson.annotations.SerializedName

data class ShippingQuery(
    val id: String,
    val from: User,
    @SerializedName("invoice_payload") val invoicePayload: String,
    @SerializedName("shipping_address") val shippingAddress: ShippingAddress
)
