package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.dispatcher.handlers.HandleGame
import com.github.kotlintelegrambot.entities.Game
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

internal class GameHandler(
    handleGame: HandleGame
) : MediaHandler<Game>(
    handleGame,
    GameHandlerFunctions::mapMessageToGame,
    GameHandlerFunctions::isUpdateGame
)

private object GameHandlerFunctions {

    fun mapMessageToGame(message: Message): Game {
        val game = message.game
        checkNotNull(game)
        return game
    }

    fun isUpdateGame(update: Update): Boolean = update.message?.game != null
}
