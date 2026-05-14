package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents reaction changes on a message with anonymous reactions.
 *
 * See https://core.telegram.org/bots/api#messagereactioncountupdated
 */
data class MessageReactionCountUpdated(
    @SerializedName("chat") val chat: Chat,
    @SerializedName("message_id") val messageId: Long,
    @SerializedName("date") val date: Long,
    @SerializedName("reactions") val reactions: List<ReactionCount>,
)
