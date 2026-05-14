package com.github.kotlintelegrambot.network.serialization.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

/**
 * Generic polymorphic deserializer for Telegram Bot API sealed types that carry a string
 * discriminator field (always `type` except for `ChatBoostSource`, which uses `source`).
 *
 * Replaces a dozen near-identical adapters that all read the discriminator, switch on its value,
 * and delegate to `context.deserialize(...)` for the concrete subtype.
 */
internal class PolymorphicTypeAdapter<T : Any>(
    private val discriminator: String = "type",
    private val variants: Map<String, Class<out T>>,
) : JsonDeserializer<T> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): T {
        val value = json.asJsonObject.get(discriminator)?.asString
            ?: throw JsonParseException("Missing discriminator '$discriminator' on $json")
        val concrete = variants[value]
            ?: throw JsonParseException("Unknown discriminator value '$value' for $discriminator")
        return context.deserialize(json, concrete)
    }
}
