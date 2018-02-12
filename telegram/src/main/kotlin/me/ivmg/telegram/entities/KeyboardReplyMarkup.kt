package me.ivmg.telegram.entities

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName as Name

data class KeyboardReplyMarkup(
    val keyboard: List<List<String>>,
    @Name("resize_keyboard") val resizeKeyboard: Boolean = false,
    @Name("one_time_keyboard") val oneTimeKeyboard: Boolean = false,
    val selective: Boolean? = null
) : ReplyMarkup {
    constructor(
        vararg keyboard: String,
        resizeKeyboard: Boolean = false,
        oneTimeKeyboard: Boolean = false,
        selective: Boolean? = null
    ) : this(listOf(keyboard.toList()), resizeKeyboard, oneTimeKeyboard, selective)

    private companion object {
        val GSON = Gson()
    }

    override fun toString(): String = GSON.toJson(this)
}