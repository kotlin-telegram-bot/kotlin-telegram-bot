package com.github.kotlintelegrambot.network.serialization.adapter

import com.github.kotlintelegrambot.entities.ChatId
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Serializes a [ChatId] as a bare JSON primitive — number for [ChatId.Id], string for
 * [ChatId.ChannelUsername] — matching the Telegram Bot API wire format inside JSON wrapper
 * objects like `ReplyParameters` or `InputMessageContent`.
 */
internal class ChatIdAdapter : JsonSerializer<ChatId> {
    override fun serialize(src: ChatId, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
        when (src) {
            is ChatId.Id -> JsonPrimitive(src.id)
            is ChatId.ChannelUsername -> JsonPrimitive(src.username)
        }
}
