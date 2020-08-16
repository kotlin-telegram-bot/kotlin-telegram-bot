package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.dispatcher.handlers.HandleVideo
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.Video

internal class VideoHandler(
    handleVideo: HandleVideo
) : MediaHandler<Video>(
    handleVideo,
    VideoHandlerFunctions::mapMessageToVideo,
    VideoHandlerFunctions::isUpdateVideo
)

private object VideoHandlerFunctions {

    fun mapMessageToVideo(message: Message): Video {
        val video = message.video
        checkNotNull(video)
        return video
    }

    fun isUpdateVideo(update: Update): Boolean = update.message?.video != null
}
