package me.ivmg.telegram.dispatcher.handlers.media

import me.ivmg.telegram.HandleAudioUpdate
import me.ivmg.telegram.entities.Audio
import me.ivmg.telegram.entities.Update

class AudioHandler(
    handleAudioUpdate: HandleAudioUpdate
) : MediaHandler<Audio>(
    handleAudioUpdate,
    AudioHandlerFunctions::toMedia,
    AudioHandlerFunctions::predicate
)

private object AudioHandlerFunctions {

    fun toMedia(update: Update): Audio {
        val audio = update.message?.audio
        checkNotNull(audio)
        return audio
    }

    fun predicate(update: Update): Boolean = update.message?.audio != null
}
