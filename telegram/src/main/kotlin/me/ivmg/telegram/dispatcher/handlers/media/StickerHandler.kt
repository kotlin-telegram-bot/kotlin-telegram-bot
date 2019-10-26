package me.ivmg.telegram.dispatcher.handlers.media

import me.ivmg.telegram.HandleStickerUpdate
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.entities.stickers.Sticker

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