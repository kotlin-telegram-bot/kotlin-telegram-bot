package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class InlineKeyboardMarkup(
    @SerializedName("inline_keyboard") val inlineKeyboard: List<List<InlineKeyboardButton>>
) : ReplyMarkup {
    companion object {
        private val GSON = Gson()

        fun createSingleButton(button: InlineKeyboardButton) = InlineKeyboardMarkup(listOf(listOf(button)))
    }

    override fun toString(): String = GSON.toJson(this)
}
