package com.github.kotlintelegrambot.network.retrofit.converters

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatId.ChannelUsername
import com.github.kotlintelegrambot.entities.ChatId.Id
import com.github.kotlintelegrambot.network.PLAIN_TEXT_MIME
import okhttp3.RequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

internal class ChatIdConverterFactory : Converter.Factory() {
    override fun stringConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ChatId, String>? {
        if (type !== ChatId::class.java) {
            return null
        }
        return Converter { chatId -> chatIdToString(chatId) }
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ChatId, RequestBody>? {
        if (type !== ChatId::class.java) {
            return null
        }
        return Converter { chatId -> RequestBody.create(PLAIN_TEXT_MIME, chatIdToString(chatId)) }
    }

    companion object {
        fun chatIdToString(chatId: ChatId) = when (chatId) {
            is Id -> chatId.id.toString()
            is ChannelUsername -> chatId.username
        }
    }
}
