package com.github.kotlintelegrambot.entities.polls

import com.github.kotlintelegrambot.entities.MessageEntity
import com.github.kotlintelegrambot.entities.ParseMode
import com.google.gson.annotations.SerializedName as Name

/**
 * Contains information about one answer option in a poll to be sent. (Bot API 7.3)
 *
 * https://core.telegram.org/bots/api#inputpolloption
 */
data class InputPollOption(
    @Name("text") val text: String,
    @Name("text_parse_mode") val textParseMode: ParseMode? = null,
    @Name("text_entities") val textEntities: List<MessageEntity>? = null,
)
