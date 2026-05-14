package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Describes a service message about a change in the price of paid messages within a chat. (Bot API 9.0)
 *
 * See https://core.telegram.org/bots/api#paidmessagepricechanged
 */
data class PaidMessagePriceChanged(
    /** The new number of Telegram Stars that must be paid by non-administrator users of the supergroup chat for each sent message. */
    @SerializedName("paid_message_star_count") val paidMessageStarCount: Int,
)
