package me.ivmg.telegram.dispatcher.handlers.media

import me.ivmg.telegram.HandleGameUpdate
import me.ivmg.telegram.entities.Game
import me.ivmg.telegram.entities.Update

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
