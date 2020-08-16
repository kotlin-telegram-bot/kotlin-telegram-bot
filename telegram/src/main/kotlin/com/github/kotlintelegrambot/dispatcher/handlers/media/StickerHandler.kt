package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.dispatcher.handlers.HandleSticker
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.stickers.Sticker

internal class StickerHandler(
    handleSticker: HandleSticker
) : MediaHandler<Sticker>(
    handleSticker,
    StickerHandlerFunctions::mapMessageToSticker,
    StickerHandlerFunctions::isUpdateSticker
)

private object StickerHandlerFunctions {

    fun mapMessageToSticker(message: Message): Sticker {
        val sticker = message.sticker
        checkNotNull(sticker)
        return sticker
    }

    fun isUpdateSticker(update: Update): Boolean = update.message?.sticker != null
}
