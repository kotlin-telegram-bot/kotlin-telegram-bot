package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Describes the physical address of a location. (Bot API 9.0)
 *
 * See https://core.telegram.org/bots/api#locationaddress
 */
data class LocationAddress(
    /** The two-letter ISO 3166-1 alpha-2 country code of the country where the location is located. */
    @SerializedName("country_code") val countryCode: String,
    @SerializedName("state") val state: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("street") val street: String? = null,
)
