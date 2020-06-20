package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.HandleVideoUpdate
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.Video

class VideoHandler(
    handleVideoUpdate: HandleVideoUpdate
) : MediaHandler<Video>(
    handleVideoUpdate,
    VideoHandlerFunctions::toMedia,
    VideoHandlerFunctions::predicate
)

private object VideoHandlerFunctions {

    fun toMedia(update: Update): Video {
        val video = update.message?.video
        checkNotNull(video)
        return video
    }

    fun predicate(update: Update): Boolean = update.message?.video != null
}
