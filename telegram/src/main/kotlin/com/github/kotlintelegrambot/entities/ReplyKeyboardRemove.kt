package com.github.kotlintelegrambot.entities

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

public class ReplyKeyboardRemove constructor(
    @SerializedName("remove_keyboard")
    public val removeKeyboard: Boolean = true,
    public val selective: Boolean? = null
) : ReplyMarkup {

    private companion object {
        val GSON = Gson()
    }

    override fun toString(): String = GSON.toJson(this)
}
