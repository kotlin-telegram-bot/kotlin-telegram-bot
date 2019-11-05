package me.ivmg.telegram.dispatcher.handlers.media

import me.ivmg.telegram.HandleVideoUpdate
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.entities.Video

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
