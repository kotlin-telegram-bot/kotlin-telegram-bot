package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents a story.
 *
 * See https://core.telegram.org/bots/api#story
 */
data class Story(
    @SerializedName("chat") val chat: Chat,
    @SerializedName("id") val id: Int,
)
