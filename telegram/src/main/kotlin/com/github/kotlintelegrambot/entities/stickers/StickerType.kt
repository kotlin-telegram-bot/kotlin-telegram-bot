package com.github.kotlintelegrambot.entities.stickers

import com.google.gson.annotations.SerializedName

/**
 * Type of a sticker. The exact values supported by Telegram are `regular`, `mask` and
 * `custom_emoji`.
 *
 * See https://core.telegram.org/bots/api#sticker
 */
enum class StickerType {
    @SerializedName("regular")
    REGULAR,

    @SerializedName("mask")
    MASK,

    @SerializedName("custom_emoji")
    CUSTOM_EMOJI,
}
