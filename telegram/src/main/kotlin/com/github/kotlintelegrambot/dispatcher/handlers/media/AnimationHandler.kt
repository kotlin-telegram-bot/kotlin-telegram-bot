package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.HandleAnimationUpdate
import com.github.kotlintelegrambot.entities.Animation
import com.github.kotlintelegrambot.entities.Update

class AnimationHandler(
    handleAnimationUpdate: HandleAnimationUpdate
) : MediaHandler<Animation>(
    handleAnimationUpdate,
    AnimationHandlerFunctions::toMedia,
    AnimationHandlerFunctions::predicate
)

private object AnimationHandlerFunctions {

    fun toMedia(update: Update): Animation {
        val animation = update.message?.animation
        checkNotNull(animation)
        return animation
    }

    fun predicate(update: Update): Boolean = update.message?.animation != null
}
