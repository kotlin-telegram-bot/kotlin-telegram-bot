package com.github.kotlintelegrambot.entities.guest

import com.google.gson.annotations.SerializedName

/**
 * Describes a message sent by a guest. (Bot API 10.0)
 *
 * See https://core.telegram.org/bots/api#sentguestmessage
 */
data class SentGuestMessage(
    @SerializedName("chat") val chat: com.github.kotlintelegrambot.entities.Chat,
    @SerializedName("message_id") val messageId: Long,
)
