package com.github.kotlintelegrambot.network.serialization.adapter

import com.github.kotlintelegrambot.entities.reaction.ReactionType
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

internal class ReactionTypeAdapter : JsonSerializer<ReactionType> {

    override fun serialize(
        src: ReactionType,
        typeOfSrc: Type,
        context: JsonSerializationContext,
    ): JsonElement = when (src) {
        is ReactionType.Custom -> context.serialize(src, ReactionType.Custom::class.java)
        is ReactionType.Emoji -> context.serialize(src, ReactionType.Emoji::class.java)
    }
}
