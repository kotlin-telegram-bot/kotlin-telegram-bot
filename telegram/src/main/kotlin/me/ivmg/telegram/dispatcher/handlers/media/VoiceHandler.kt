package me.ivmg.telegram.dispatcher.handlers.media

import me.ivmg.telegram.HandleVoiceUpdate
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.entities.Voice

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
