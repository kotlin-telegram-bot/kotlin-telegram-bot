package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Service message about a new managed bot created in the chat. (Bot API 9.6)
 *
 * See https://core.telegram.org/bots/api#managedbotcreated
 */
data class ManagedBotCreated(
    @SerializedName("bot") val bot: com.github.kotlintelegrambot.entities.User,
)
