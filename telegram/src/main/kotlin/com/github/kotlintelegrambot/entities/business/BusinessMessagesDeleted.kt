package com.github.kotlintelegrambot.entities.business

import com.github.kotlintelegrambot.entities.Chat
import com.google.gson.annotations.SerializedName as Name

/**
 * Notifies that messages were deleted from a connected business account.
 *
 * https://core.telegram.org/bots/api#businessmessagesdeleted (Bot API 7.2)
 */
data class BusinessMessagesDeleted(
    @Name("business_connection_id") val businessConnectionId: String,
    @Name("chat") val chat: Chat,
    @Name("message_ids") val messageIds: List<Long>,
)
