package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Service message about the chat owner being changed. (Bot API 9.4)
 *
 * See https://core.telegram.org/bots/api#chatownerchanged
 */
data class ChatOwnerChanged(
    @SerializedName("new_owner") val newOwner: com.github.kotlintelegrambot.entities.User,
)
