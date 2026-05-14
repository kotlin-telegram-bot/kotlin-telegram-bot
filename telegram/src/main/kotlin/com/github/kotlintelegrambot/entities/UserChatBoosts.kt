package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents a list of boosts added to a chat by a user.
 *
 * See https://core.telegram.org/bots/api#userchatboosts
 */
data class UserChatBoosts(
    @SerializedName("boosts") val boosts: List<ChatBoost>,
)
