package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.HandleAudioUpdate
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.Audio

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
