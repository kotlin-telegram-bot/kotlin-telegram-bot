package com.github.kotlintelegrambot.entities.keyboard

import com.google.gson.annotations.SerializedName

/**
 * Represents one button of an inline keyboard ([loginUrl] not supported yet).
 * @see <https://core.telegram.org/bots/api#inlinekeyboardbutton>
 */
sealed class InlineKeyboardButton {
    abstract val text: String

    /**
     * HTTP or tg:// url to be opened when button is pressed.
     */
    data class Url(
        override val text: String,
        val url: String
    ) : InlineKeyboardButton()

    /**
     * Data to be sent in a callback query to the bot when button is pressed (1-64 bytes).
     */
    data class CallbackData(
        override val text: String,
        @SerializedName("callback_data") val callbackData: String
    ) : InlineKeyboardButton()

    /**
     * Pressing the button will prompt the user to select one of their chats, open the chat and
     * insert the bot's username and the specified inline query in the input field. Can be empty,
     * in which case just the bot's username will be inserted.
     */
    data class SwitchInlineQuery(
        override val text: String,
        @SerializedName("switch_inline_query") val switchInlineQuery: String
    ) : InlineKeyboardButton()

    /**
     * Pressing the button will insert the bot's username and the specified inline query in the
     * current chat's input field. Can be empty, in which case only the bot's username will be
     * inserted.
     */
    data class SwitchInlineQueryCurrentChat(
        override val text: String,
        @SerializedName("switch_inline_query_current_chat") val switchInlineQueryCurrentChat: String
    ) : InlineKeyboardButton()

    /**
     * To send a pay button.
     * NOTE: this type of button must always be the first button in the first row.
     */
    data class Pay(override val text: String) : InlineKeyboardButton() {
        val pay = true
    }
}
