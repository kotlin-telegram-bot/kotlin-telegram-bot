package com.github.kotlintelegrambot.entities.gifts

import com.google.gson.annotations.SerializedName

/**
 * Describes the types of gifts that can be gifted to a user or a chat. (Bot API 9.0)
 *
 * See https://core.telegram.org/bots/api#acceptedgifttypes
 */
data class AcceptedGiftTypes(
    @SerializedName("unlimited_gifts") val unlimitedGifts: Boolean,
    @SerializedName("limited_gifts") val limitedGifts: Boolean,
    @SerializedName("unique_gifts") val uniqueGifts: Boolean,
    @SerializedName("premium_subscription") val premiumSubscription: Boolean,
)
