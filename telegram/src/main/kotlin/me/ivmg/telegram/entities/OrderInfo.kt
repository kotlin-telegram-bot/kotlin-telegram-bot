package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class OrderInfo(
    val name: String?,
    @Name("phone_number") val phoneNumber: String?,
    val email: String?,
    @Name("shipping_address") val shippingAddress: ShippingAddress?
)