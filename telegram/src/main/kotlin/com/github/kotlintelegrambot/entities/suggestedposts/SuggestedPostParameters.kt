package com.github.kotlintelegrambot.entities.suggestedposts

import com.google.gson.annotations.SerializedName

/**
 * Contains parameters of a post that is being suggested by the bot. (Bot API 9.2)
 *
 * See https://core.telegram.org/bots/api#suggestedpostparameters
 */
data class SuggestedPostParameters(
    @SerializedName("price") val price: SuggestedPostPrice? = null,
    @SerializedName("send_date") val sendDate: Long? = null,
)

/**
 * Describes the price of a suggested post. (Bot API 9.2)
 *
 * See https://core.telegram.org/bots/api#suggestedpostprice
 */
data class SuggestedPostPrice(
    @SerializedName("currency") val currency: String,
    @SerializedName("amount") val amount: Long,
)
