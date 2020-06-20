package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.HandleVideoNoteUpdate
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.VideoNote

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
