package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Contains information about a prepared keyboard button. (Bot API 9.6)
 *
 * See https://core.telegram.org/bots/api#preparedkeyboardbutton
 */
data class PreparedKeyboardButton(
    @SerializedName("id") val id: String,
    @SerializedName("expiration_date") val expirationDate: Long,
)
