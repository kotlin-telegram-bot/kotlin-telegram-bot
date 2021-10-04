package com.github.kotlintelegrambot.network.retrofit.converters

import com.github.kotlintelegrambot.entities.inputmedia.InputMedia
import com.google.gson.Gson
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

internal class InputMediaConverterFactory(private val gson: Gson) : Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<InputMedia, String>? {
        if (type != InputMedia::class.java) {
            return null
        }
        return Converter { inputMedia ->
            gson.toJson(inputMedia)
        }
    }
}
