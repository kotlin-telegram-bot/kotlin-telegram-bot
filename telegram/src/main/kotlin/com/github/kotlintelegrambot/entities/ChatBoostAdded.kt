package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

/**
 * Represents the service message that a user boosted the chat.
 *
 * https://core.telegram.org/bots/api#chatboostadded (Bot API 7.1)
 */
data class ChatBoostAdded(
    @Name("boost_count") val boostCount: Int,
)
