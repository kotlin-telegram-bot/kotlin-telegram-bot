package me.ivmg.telegram.entities.payment

import com.google.gson.annotations.SerializedName
import me.ivmg.telegram.entities.User

data class ShippingQuery(
    val id: String,
    val from: User,
    @SerializedName("invoice_payload") val invoicePayload: String,
    @SerializedName("shipping_address") val shippingAddress: ShippingAddress
)