package com.github.kotlintelegrambot.network.serialization.adapter

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton.CallbackData
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton.Pay
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton.SwitchInlineQuery
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton.SwitchInlineQueryCurrentChat
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton.Url
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

class InlineKeyboardButtonAdapter : JsonSerializer<InlineKeyboardButton>, JsonDeserializer<InlineKeyboardButton> {

    private class InlineKeyboardButtonDto(
        val text: String,
        val url: String? = null,
        @SerializedName("callback_data") val callbackData: String? = null,
        @SerializedName("switch_inline_query") val switchInlineQuery: String? = null,
        @SerializedName("switch_inline_query_current_chat") val switchInlineQueryCurrentChat: String? = null,
        val pay: Boolean? = null
    )

    override fun serialize(
        src: InlineKeyboardButton,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement = when (src) {
        is Url -> context.serialize(src, Url::class.java)
        is CallbackData -> context.serialize(src, CallbackData::class.java)
        is SwitchInlineQuery -> context.serialize(src, SwitchInlineQuery::class.java)
        is SwitchInlineQueryCurrentChat -> context.serialize(src, SwitchInlineQueryCurrentChat::class.java)
        is Pay -> context.serialize(src, Pay::class.java)
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): InlineKeyboardButton {
        val inlineKeyboardButtonDto = context.deserialize<InlineKeyboardButtonDto>(
            json,
            InlineKeyboardButtonDto::class.java
        )

        return with(inlineKeyboardButtonDto) {
            when {
                url != null -> Url(text, url)
                callbackData != null -> CallbackData(text, callbackData)
                switchInlineQuery != null -> SwitchInlineQuery(text, switchInlineQuery)
                switchInlineQueryCurrentChat != null -> SwitchInlineQueryCurrentChat(
                    text,
                    switchInlineQueryCurrentChat
                )
                pay != null -> Pay(text)
                else -> error("unsupported inline keyboard button $inlineKeyboardButtonDto")
            }
        }
    }
}
