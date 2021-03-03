package com.github.kotlintelegrambot.network.retrofit.converters

import com.github.kotlintelegrambot.entities.dice.DiceEmoji
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

internal class DiceEmojiConverterFactory : Converter.Factory() {

    override fun stringConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<DiceEmoji, String>? {
        val clazz = type as? Class<*> ?: return null
        val diceEmojiSuperclass = DiceEmoji::class.java

        if (clazz != diceEmojiSuperclass && clazz.genericSuperclass != diceEmojiSuperclass) {
            return null
        }

        return Converter { diceEmoji: DiceEmoji -> diceEmoji.emojiValue }
    }
}
