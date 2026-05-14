package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Contains information about the quoted part of a message that is replied to by the given message.
 *
 * See https://core.telegram.org/bots/api#textquote
 */
data class TextQuote(
    @SerializedName("text") val text: String,
    @SerializedName("entities") val entities: List<MessageEntity>? = null,
    @SerializedName("position") val position: Int,
    @SerializedName("is_manual") val isManual: Boolean? = null,
)
