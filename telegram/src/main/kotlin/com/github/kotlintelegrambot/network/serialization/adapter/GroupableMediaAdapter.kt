package com.github.kotlintelegrambot.network.serialization.adapter

import com.github.kotlintelegrambot.entities.inputmedia.GroupableMedia
import com.github.kotlintelegrambot.entities.inputmedia.InputMedia
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaAudio
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaDocument
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaPhoto
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaVideo
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

internal class GroupableMediaAdapter(
    private val inputMediaAdapter: InputMediaAdapter
) : JsonSerializer<GroupableMedia> {
    override fun serialize(src: GroupableMedia?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
        when (src) {
            is InputMediaVideo,
            is InputMediaPhoto,
            is InputMediaAudio,
            is InputMediaDocument -> inputMediaAdapter.serialize(src as InputMedia, typeOfSrc, context)
            else -> throw IllegalArgumentException("unknown groupable media $src")
        }
}
