package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

data class OrderInfo(
    val name: String? = null,
    @Name("phone_number") val phoneNumber: String? = null,
    val email: String? = null,
    @Name("shipping_address") val shippingAddress: ShippingAddress? = null
)
