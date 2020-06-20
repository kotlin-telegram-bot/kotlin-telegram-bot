package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.HandleVoiceUpdate
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.Voice

class VoiceHandler(
    handleVoiceUpdate: HandleVoiceUpdate
) : MediaHandler<Voice>(
    handleVoiceUpdate,
    VoiceHandlerFunctions::toMedia,
    VoiceHandlerFunctions::predicate
)

private object VoiceHandlerFunctions {

    fun toMedia(update: Update): Voice {
        val voice = update.message?.voice
        checkNotNull(voice)
        return voice
    }

    fun predicate(update: Update): Boolean = update.message?.voice != null
}
