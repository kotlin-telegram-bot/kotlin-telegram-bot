package me.ivmg.telegram.dispatcher.handlers.media

import me.ivmg.telegram.HandleVideoNoteUpdate
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.entities.VideoNote

class VideoNoteHandler(
    handleVideoNoteUpdate: HandleVideoNoteUpdate
) : MediaHandler<VideoNote>(
    handleVideoNoteUpdate,
    VideoNoteHandlerFunctions::toMedia,
    VideoNoteHandlerFunctions::predicate
)

private object VideoNoteHandlerFunctions {

    fun toMedia(update: Update): VideoNote {
        val videoNote = update.message?.videoNote
        checkNotNull(videoNote)
        return videoNote
    }

    fun predicate(update: Update): Boolean = update.message?.videoNote != null
}
