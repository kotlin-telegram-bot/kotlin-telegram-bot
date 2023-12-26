package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChosenInlineResult
import com.github.kotlintelegrambot.entities.Update

data class ChosenInlineResultHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val chosenInlineResult: ChosenInlineResult,
)

class ChosenInlineResultHandler(
    private val handleChosenInlineResult: HandleChosenInlineResult,
) : Handler {

    override fun checkUpdate(update: Update): Boolean {
        return update.chosenInlineResult != null
    }

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        checkNotNull(update.chosenInlineResult)

        val contactHandlerEnv = ChosenInlineResultHandlerEnvironment(
            bot,
            update,
            update.chosenInlineResult,
        )
        handleChosenInlineResult(contactHandlerEnv)
    }
}
