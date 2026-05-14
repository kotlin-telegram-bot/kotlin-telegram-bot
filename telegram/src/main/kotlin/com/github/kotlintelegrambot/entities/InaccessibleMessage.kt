package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * This object describes a message that was deleted or is otherwise inaccessible to the bot.
 *
 * See https://core.telegram.org/bots/api#inaccessiblemessage
 */
data class InaccessibleMessage(
    @SerializedName("chat") val chat: Chat,
    @SerializedName("message_id") val messageId: Long,
    @SerializedName("date") val date: Long,
)
