package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents a boost removed from a chat.
 *
 * See https://core.telegram.org/bots/api#chatboostremoved
 */
data class ChatBoostRemoved(
    @SerializedName("chat") val chat: Chat,
    @SerializedName("boost_id") val boostId: String,
    @SerializedName("remove_date") val removeDate: Long,
    @SerializedName("source") val source: ChatBoostSource,
)
