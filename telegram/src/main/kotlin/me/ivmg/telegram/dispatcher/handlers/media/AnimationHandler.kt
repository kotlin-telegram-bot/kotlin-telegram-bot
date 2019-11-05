package me.ivmg.telegram.dispatcher.handlers.media

import me.ivmg.telegram.HandleAnimationUpdate
import me.ivmg.telegram.entities.Animation
import me.ivmg.telegram.entities.Update

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
