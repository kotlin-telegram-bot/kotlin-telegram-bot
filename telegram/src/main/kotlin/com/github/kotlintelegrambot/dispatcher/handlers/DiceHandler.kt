package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.HandleDice
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.entities.Update

internal class DiceHandler(handleDice: HandleDice) : Handler(HandleDiceProxy(handleDice)) {
    override val groupIdentifier: String
        get() = "dice"

    override fun checkUpdate(update: Update): Boolean = update.message?.dice != null
}

private class HandleDiceProxy(private val handleDice: HandleDice) : HandleUpdate {
    override fun invoke(bot: Bot, update: Update) {
        val message = update.message
        val dice = message?.dice
        checkNotNull(dice)
        handleDice.invoke(bot, message, dice)
    }
}
