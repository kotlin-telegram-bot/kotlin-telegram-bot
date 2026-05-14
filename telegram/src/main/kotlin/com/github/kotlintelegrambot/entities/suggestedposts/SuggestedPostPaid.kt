package com.github.kotlintelegrambot.entities.suggestedposts

import com.google.gson.annotations.SerializedName

/**
 * Describes a service message about a successful payment for a suggested post. (Bot API 9.2)
 *
 * See https://core.telegram.org/bots/api#suggestedpostpaid
 */
data class SuggestedPostPaid(
    @SerializedName("suggested_post_message") val suggestedPostMessage: com.github.kotlintelegrambot.entities.Message? = null,
    @SerializedName("currency") val currency: String,
    @SerializedName("amount") val amount: Long? = null,
    @SerializedName("star_amount") val starAmount: com.github.kotlintelegrambot.entities.payments.StarAmount? = null,
)
