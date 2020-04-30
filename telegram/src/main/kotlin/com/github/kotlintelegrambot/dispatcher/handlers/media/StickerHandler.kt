package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.HandleStickerUpdate
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.stickers.Sticker

class StickerHandler(
    handleStickerUpdate: HandleStickerUpdate
) : MediaHandler<Sticker>(
    handleStickerUpdate,
    StickerHandlerFunctions::toMedia,
    StickerHandlerFunctions::predicate
)

private object StickerHandlerFunctions {

    fun toMedia(update: Update): Sticker {
        val sticker = update.message?.sticker
        checkNotNull(sticker)
        return sticker
    }

    fun predicate(update: Update): Boolean = update.message?.sticker != null
}
