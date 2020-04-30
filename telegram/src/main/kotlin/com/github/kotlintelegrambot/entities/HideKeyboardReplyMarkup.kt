package com.github.kotlintelegrambot.entities

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName as Name

data class HideKeyboardReplyMarkup(
    @Name("hide_keyboard") val hideKeyboard: Boolean = true,
    val selective: Boolean? = null
) : ReplyMarkup {
    private companion object {
        val GSON = Gson()
    }

    override fun toString(): String = GSON.toJson(this)
}
