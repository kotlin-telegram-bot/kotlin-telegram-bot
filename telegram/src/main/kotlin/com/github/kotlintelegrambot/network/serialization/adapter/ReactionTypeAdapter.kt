package com.github.kotlintelegrambot.network.serialization.adapter

import com.github.kotlintelegrambot.entities.reaction.ReactionType
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

internal class ReactionTypeAdapter :
    JsonSerializer<ReactionType>,
    JsonDeserializer<ReactionType> {

    override fun serialize(
        src: ReactionType,
        typeOfSrc: Type,
        context: JsonSerializationContext,
    ): JsonElement = when (src) {
        is ReactionType.Custom -> context.serialize(src, ReactionType.Custom::class.java)
        is ReactionType.Emoji -> context.serialize(src, ReactionType.Emoji::class.java)
        is ReactionType.Paid -> context.serialize(src, ReactionType.Paid::class.java)
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): ReactionType {
        val obj = json.asJsonObject
        return when (val type = obj.get("type").asString) {
            "emoji" -> context.deserialize(json, ReactionType.Emoji::class.java)
            "custom_emoji" -> context.deserialize(json, ReactionType.Custom::class.java)
            "paid" -> context.deserialize(json, ReactionType.Paid::class.java)
            else -> throw JsonParseException("Unknown ReactionType: $type")
        }
    }
}
