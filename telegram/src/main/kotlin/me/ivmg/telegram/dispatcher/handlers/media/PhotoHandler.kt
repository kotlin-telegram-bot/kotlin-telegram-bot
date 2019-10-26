package me.ivmg.telegram.dispatcher.handlers.media

import me.ivmg.telegram.HandlePhotosUpdate
import me.ivmg.telegram.entities.PhotoSize
import me.ivmg.telegram.entities.Update

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