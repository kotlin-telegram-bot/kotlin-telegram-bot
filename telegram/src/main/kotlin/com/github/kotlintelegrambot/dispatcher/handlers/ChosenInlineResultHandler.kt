package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChosenInlineResult
import com.github.kotlintelegrambot.entities.Update

data class ChosenInlineResultHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val chosenInlineResult: ChosenInlineResult
)

internal class ChosenInlineResultHandler(
    private val handleChosenInlineResult: HandleChosenInlineResult
) : Handler {

    override fun checkUpdate(update: Update) = update.chosenInlineResult != null

    override fun handleUpdate(bot: Bot, update: Update)
    {
        val chosenInlineResult = update.chosenInlineResult
        checkNotNull(chosenInlineResult)

        val inlineResultEnv = ChosenInlineResultHandlerEnvironment(bot, update, chosenInlineResult)
        handleChosenInlineResult(inlineResultEnv)
    }
}
