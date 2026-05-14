package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Describes a service message about a change in the price of direct messages sent to a channel
 * chat. (Bot API 9.1)
 *
 * See https://core.telegram.org/bots/api#directmessagepricechanged
 */
data class DirectMessagePriceChanged(
    @SerializedName("are_direct_messages_enabled") val areDirectMessagesEnabled: Boolean,
    @SerializedName("direct_message_star_count") val directMessageStarCount: Int? = null,
)
