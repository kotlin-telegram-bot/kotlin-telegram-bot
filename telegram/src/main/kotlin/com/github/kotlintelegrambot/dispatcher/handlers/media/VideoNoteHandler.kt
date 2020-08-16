package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.dispatcher.handlers.HandleVideoNote
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.VideoNote

internal class VideoNoteHandler(
    handleVideoNote: HandleVideoNote
) : MediaHandler<VideoNote>(
    handleVideoNote,
    VideoNoteHandlerFunctions::mapMessageToVideoNote,
    VideoNoteHandlerFunctions::isUpdateVideoNote
)

private object VideoNoteHandlerFunctions {

    fun mapMessageToVideoNote(message: Message): VideoNote {
        val videoNote = message.videoNote
        checkNotNull(videoNote)
        return videoNote
    }

    fun isUpdateVideoNote(update: Update): Boolean = update.message?.videoNote != null
}
