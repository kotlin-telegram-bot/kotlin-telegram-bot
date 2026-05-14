package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Defines the criteria used to request a managed bot from the user. (Bot API 9.6)
 *
 * See https://core.telegram.org/bots/api#keyboardbuttonrequestmanagedbot
 */
data class KeyboardButtonRequestManagedBot(
    @SerializedName("request_id") val requestId: Int,
    @SerializedName("name_max_length") val nameMaxLength: Int? = null,
)
