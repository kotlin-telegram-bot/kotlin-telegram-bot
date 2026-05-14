package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Service message about a managed bot being updated. (Bot API 9.6)
 *
 * See https://core.telegram.org/bots/api#managedbotupdated
 */
data class ManagedBotUpdated(
    @SerializedName("bot") val bot: com.github.kotlintelegrambot.entities.User,
)
