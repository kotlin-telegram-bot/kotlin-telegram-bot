package com.github.kotlintelegrambot.entities.payments

import com.google.gson.annotations.SerializedName

/**
 *
 * This object represents a shipping address.
 *
 * @property [countryCode] ISO 3166-1 alpha-2 country code
 * @property [state] State, if applicable
 * @property [city] City
 * @property [streetLine1] First line for the address
 * @property [streetLine2] Second line for the address
 * @property [postCode] Address post code
 */
data class ShippingAddress(
    @SerializedName("country_code") val countryCode: String,
    val state: String,
    val city: String,
    @SerializedName("street_line1") val streetLine1: String,
    @SerializedName("street_line2") val streetLine2: String,
    @SerializedName("post_code") val postCode: String
)
