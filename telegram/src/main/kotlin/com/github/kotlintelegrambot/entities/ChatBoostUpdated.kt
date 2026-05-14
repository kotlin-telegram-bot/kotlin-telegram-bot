package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents a boost added to a chat or changed.
 *
 * See https://core.telegram.org/bots/api#chatboostupdated
 */
data class ChatBoostUpdated(
    @SerializedName("chat") val chat: Chat,
    @SerializedName("boost") val boost: ChatBoost,
)
