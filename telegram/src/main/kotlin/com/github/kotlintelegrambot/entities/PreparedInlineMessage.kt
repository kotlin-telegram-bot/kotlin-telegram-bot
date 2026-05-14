package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Describes an inline message to be sent by a user of a Mini App. (Bot API 8.0)
 *
 * See https://core.telegram.org/bots/api#preparedinlinemessage
 */
data class PreparedInlineMessage(
    @SerializedName("id") val id: String,
    @SerializedName("expiration_date") val expirationDate: Long,
)
