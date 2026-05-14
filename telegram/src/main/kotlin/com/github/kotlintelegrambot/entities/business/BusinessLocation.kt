package com.github.kotlintelegrambot.entities.business

import com.github.kotlintelegrambot.entities.Location
import com.google.gson.annotations.SerializedName as Name

/**
 * Describes the location of a business account.
 *
 * https://core.telegram.org/bots/api#businesslocation (Bot API 7.2)
 */
data class BusinessLocation(
    @Name("address") val address: String,
    @Name("location") val location: Location? = null,
)
