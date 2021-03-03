package com.github.kotlintelegrambot.network.serialization.adapter

import com.github.kotlintelegrambot.entities.inputmedia.InputMedia
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaAnimation
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaAudio
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaDocument
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaFields
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaPhoto
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaVideo
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

internal class InputMediaAdapter : JsonSerializer<InputMedia> {
    override fun serialize(src: InputMedia, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonElement = when (src) {
            is InputMediaPhoto -> context.serialize(src, InputMediaPhoto::class.java)
            is InputMediaVideo -> context.serialize(src, InputMediaVideo::class.java)
            is InputMediaAnimation -> context.serialize(src, InputMediaAnimation::class.java)
            is InputMediaAudio -> context.serialize(src, InputMediaAudio::class.java)
            is InputMediaDocument -> context.serialize(src, InputMediaDocument::class.java)
        }
        val jsonObject = jsonElement.asJsonObject
        jsonObject.addProperty(InputMediaFields.TYPE, src.type)
        return jsonObject
    }
}
