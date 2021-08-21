package com.github.kotlintelegrambot.network.serialization.adapter

import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.TelegramFile.ByByteArray
import com.github.kotlintelegrambot.entities.TelegramFile.ByFile
import com.github.kotlintelegrambot.entities.TelegramFile.ByFileId
import com.github.kotlintelegrambot.entities.TelegramFile.ByUrl
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

internal class TelegramFileAdapter : JsonSerializer<TelegramFile> {
    override fun serialize(src: TelegramFile, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = when (src) {
        is ByFileId -> context.serialize(src.fileId, String::class.java)
        is ByUrl -> context.serialize(src.url, String::class.java)
        is ByFile -> context.serialize("attach://${src.file.name}")
        is ByByteArray -> context.serialize("attach://${src.filename!!}")
    }
}
