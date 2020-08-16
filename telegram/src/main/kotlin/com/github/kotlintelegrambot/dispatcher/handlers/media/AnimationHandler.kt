package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.dispatcher.handlers.HandleAnimation
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.Animation

internal class AnimationHandler(
    handleAnimation: HandleAnimation
) : MediaHandler<Animation>(
    handleAnimation,
    AnimationHandlerFunctions::mapMessageToAnimation,
    AnimationHandlerFunctions::updateIsAnimation
)

private object AnimationHandlerFunctions {

    fun mapMessageToAnimation(message: Message): Animation {
        checkNotNull(message.animation)
        return message.animation
    }

    fun updateIsAnimation(update: Update): Boolean = update.message?.animation != null
}
