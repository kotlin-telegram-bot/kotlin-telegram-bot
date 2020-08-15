package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.dice.Dice

data class DiceHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val message: Message,
    val dice: Dice
)

internal class DiceHandler(handleDice: HandleDice) : Handler(HandleDiceProxy(handleDice)) {
    override val groupIdentifier: String
        get() = "dice"

    override fun checkUpdate(update: Update): Boolean = update.message?.dice != null
}

private class HandleDiceProxy(private val handleDice: HandleDice) :
    HandleUpdate {
    override fun invoke(bot: Bot, update: Update) {
        val message = update.message
        val dice = message?.dice
        checkNotNull(dice)

        val diceHandlerEnv = DiceHandlerEnvironment(bot, update, message, dice)
        handleDice.invoke(diceHandlerEnv)
    }
}
