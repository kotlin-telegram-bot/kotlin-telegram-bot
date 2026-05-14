package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents a chat background. (Bot API 7.3)
 *
 * See https://core.telegram.org/bots/api#chatbackground
 */
data class ChatBackground(
    @SerializedName("type") val type: BackgroundType,
)
