package com.github.kotlintelegrambot.entities.stickers

import com.google.gson.annotations.SerializedName

/**
 * Format of a sticker. Used by `uploadStickerFile` and `createNewStickerSet` (Bot API 7.2).
 *
 * See https://core.telegram.org/bots/api#inputsticker
 */
enum class StickerFormat {
    @SerializedName("static")
    STATIC,

    @SerializedName("animated")
    ANIMATED,

    @SerializedName("video")
    VIDEO,
}
