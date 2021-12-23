package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName as Name

public data class KeyboardReplyMarkup(
    val keyboard: List<List<KeyboardButton>>,
    @Name("resize_keyboard") val resizeKeyboard: Boolean = false,
    @Name("one_time_keyboard") val oneTimeKeyboard: Boolean = false,
    val selective: Boolean? = null
) : ReplyMarkup {

    public constructor(
        vararg keyboard: KeyboardButton,
        resizeKeyboard: Boolean = false,
        oneTimeKeyboard: Boolean = false,
        selective: Boolean? = null
    ) : this(listOf(keyboard.toList()), resizeKeyboard, oneTimeKeyboard, selective)

    public companion object {
        public val GSON: Gson = Gson()

        public fun createSimpleKeyboard(
            keyboard: List<List<String>>,
            resizeKeyboard: Boolean = true,
            oneTimeKeyboard: Boolean = false,
            selective: Boolean? = null
        ): KeyboardReplyMarkup {
            return KeyboardReplyMarkup(keyboard.map { list -> list.map { KeyboardButton(text = it) } }, resizeKeyboard, oneTimeKeyboard, selective)
        }
    }

    override fun toString(): String = GSON.toJson(this)
}
