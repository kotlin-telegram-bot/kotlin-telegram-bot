package com.github.kotlintelegrambot.entities

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class ReplyKeyboardRemove constructor(
    @SerializedName("remove_keyboard")
    val removeKeyboard: Boolean = true,
    val selective: Boolean? = null
) : ReplyMarkup {

    private companion object {
        val GSON = Gson()
    }

    override fun toString(): String = GSON.toJson(this)
}
