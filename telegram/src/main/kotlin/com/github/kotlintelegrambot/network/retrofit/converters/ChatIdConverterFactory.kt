package com.github.kotlintelegrambot.network.retrofit.converters

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatId.Id
import com.github.kotlintelegrambot.entities.ChatId.Username
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ChatIdConverterFactory : Converter.Factory() {
    override fun stringConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ChatId, String>? {
        if (type !== ChatId::class.java) {
            return null
        }
        return Converter { chatId: ChatId ->
            when (chatId) {
                is Id -> chatId.id.toString()
                is Username -> chatId.username
            }
        }
    }
}
