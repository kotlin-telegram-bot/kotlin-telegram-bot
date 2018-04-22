package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class InlineKeyboardButton(
    val text: String,
    val url: String? = null,
    @Name("callback_data") val callbackData: String? = null,
    @Name("switch_inline_query") val switchInlineQuery: String? = null
) : ReplyMarkup