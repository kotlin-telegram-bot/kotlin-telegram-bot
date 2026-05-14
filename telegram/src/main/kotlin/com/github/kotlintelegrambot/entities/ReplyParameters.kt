package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Describes the message to be replied to.
 *
 * Replaces the legacy `reply_to_message_id` + `allow_sending_without_reply` send-method parameters
 * (Bot API 7.0). See https://core.telegram.org/bots/api#replyparameters
 *
 * @property messageId Identifier of the message that will be replied to in the current chat, or in
 *   the chat [chatId] if it is specified.
 * @property chatId If the message to be replied to is from a different chat, unique identifier for
 *   the chat or username of the bot, supergroup or channel.
 * @property allowSendingWithoutReply Pass `true` if the message should be sent even if the specified
 *   message to be replied to is not found.
 * @property quote Quoted part of the message to be replied to; 0-1024 characters after entities
 *   parsing. The quote must be an exact substring of the message to be replied to.
 * @property quoteParseMode Mode for parsing entities in the quote.
 * @property quoteEntities A JSON-serialized list of special entities that appear in the quote.
 * @property quotePosition Position of the quote in the original message in UTF-16 code units.
 * @property checklistTaskId Identifier of the specific checklist task to be replied to (Bot API 9.2).
 * @property pollOptionId Persistent identifier of the specific poll option to be replied to (Bot API 9.6).
 */
data class ReplyParameters(
    @SerializedName("message_id") val messageId: Long,
    @SerializedName("chat_id") val chatId: ChatId? = null,
    @SerializedName("allow_sending_without_reply") val allowSendingWithoutReply: Boolean? = null,
    @SerializedName("quote") val quote: String? = null,
    @SerializedName("quote_parse_mode") val quoteParseMode: ParseMode? = null,
    @SerializedName("quote_entities") val quoteEntities: List<MessageEntity>? = null,
    @SerializedName("quote_position") val quotePosition: Int? = null,
    @SerializedName("checklist_task_id") val checklistTaskId: Int? = null,
    @SerializedName("poll_option_id") val pollOptionId: String? = null,
)
