package com.github.kotlintelegrambot.entities.keyboard

import com.google.gson.annotations.SerializedName

/**
 * Represents an inline keyboard button that copies a specified text to the clipboard.
 * (Bot API 7.11)
 *
 * See https://core.telegram.org/bots/api#copytextbutton
 */
data class CopyTextButton(
    @SerializedName("text") val text: String,
)
