package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Describes bot access settings configured by a user for a chat. (Bot API 10.0)
 *
 * See https://core.telegram.org/bots/api#botaccesssettings
 */
data class BotAccessSettings(
    @SerializedName("allowed") val allowed: Boolean,
    @SerializedName("expiration_date") val expirationDate: Long? = null,
)
