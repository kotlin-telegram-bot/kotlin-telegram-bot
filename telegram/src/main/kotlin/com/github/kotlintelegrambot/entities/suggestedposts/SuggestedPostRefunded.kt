package com.github.kotlintelegrambot.entities.suggestedposts

import com.google.gson.annotations.SerializedName

/**
 * Describes a service message about a refund of a payment for a suggested post. (Bot API 9.2)
 *
 * See https://core.telegram.org/bots/api#suggestedpostrefunded
 */
data class SuggestedPostRefunded(
    @SerializedName("suggested_post_message") val suggestedPostMessage: com.github.kotlintelegrambot.entities.Message? = null,
    @SerializedName("reason") val reason: String,
)
