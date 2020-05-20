package com.github.kotlintelegrambot.entities.keyboard

import com.google.gson.annotations.SerializedName as Name

data class InlineKeyboardButton(
    val text: String,
    val url: String? = null,
    @Name("callback_data") val callbackData: String? = null,
    @Name("switch_inline_query") val switchInlineQuery: String? = null,
    @Name("switch_inline_query_current_chat") val switchInlineQueryCurrentChat: String? = null
)
