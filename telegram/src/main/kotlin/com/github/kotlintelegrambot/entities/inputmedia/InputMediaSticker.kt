package com.github.kotlintelegrambot.entities.inputmedia

import com.google.gson.annotations.SerializedName

/**
 * Represents a sticker to be sent. (Bot API 10.0)
 *
 * Standalone definition: not yet wired into the existing [InputMedia] sealed type. See follow-up
 * notes in the PR description.
 *
 * See https://core.telegram.org/bots/api#inputmediasticker
 */
data class InputMediaSticker(
    @SerializedName("type") val type: String = "sticker",
    @SerializedName("media") val media: String,
    @SerializedName("emoji") val emoji: String? = null,
)
