package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.dispatcher.handlers.HandleVoice
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.Voice

internal class VoiceHandler(
    handleVoice: HandleVoice
) : MediaHandler<Voice>(
    handleVoice,
    VoiceHandlerFunctions::mapMessageToVoice,
    VoiceHandlerFunctions::isUpdateVoice
)

private object VoiceHandlerFunctions {

    fun mapMessageToVoice(message: Message): Voice {
        val voice = message.voice
        checkNotNull(voice)
        return voice
    }

    fun isUpdateVoice(update: Update): Boolean = update.message?.voice != null
}
