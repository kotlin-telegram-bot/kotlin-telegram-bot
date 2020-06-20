package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.HandlePhotosUpdate
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.PhotoSize

class PhotosHandler(
    handlePhotosUpdate: HandlePhotosUpdate
) : MediaHandler<List<PhotoSize>>(
    handlePhotosUpdate,
    PhotosHandlerFunctions::toMedia,
    PhotosHandlerFunctions::predicate
)

private object PhotosHandlerFunctions {

    fun toMedia(update: Update): List<PhotoSize> {
        val photos = update.message?.photo
        checkNotNull(photos)
        return photos
    }

    fun predicate(update: Update): Boolean = update.message?.photo != null
}
