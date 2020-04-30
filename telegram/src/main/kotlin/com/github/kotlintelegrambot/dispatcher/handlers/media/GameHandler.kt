package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.HandleGameUpdate
import com.github.kotlintelegrambot.entities.Game
import com.github.kotlintelegrambot.entities.Update

class GameHandler(
    handleGameUpdate: HandleGameUpdate
) : MediaHandler<Game>(
    handleGameUpdate,
    GameHandlerFunctions::toMedia,
    GameHandlerFunctions::predicate
)

private object GameHandlerFunctions {

    fun toMedia(update: Update): Game {
        val game = update.message?.game
        checkNotNull(game)
        return game
    }

    fun predicate(update: Update): Boolean = update.message?.game != null
}
