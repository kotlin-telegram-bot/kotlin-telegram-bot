package me.ivmg.telegram.entities

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class InlineKeyboardMarkup(
    @SerializedName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboardButton>>
) : ReplyMarkup {
    private companion object {
        val GSON = Gson()
    }

    override fun toString(): String = GSON.toJson(this)
}