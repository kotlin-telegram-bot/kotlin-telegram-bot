package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.dice.Dice

data class DiceHandlerEnvironment(
    override val bot: Bot,
    override val update: Update,
    override val message: Message,
    val dice: Dice
) : IMessageHandlerEnvironment

internal class DiceHandler(
    private val handleDice: HandleDice
) : Handler {

    override fun checkUpdate(update: Update): Boolean = update.message?.dice != null

    override fun handleUpdate(bot: Bot, update: Update) {
        val message = update.message
        val dice = message?.dice
        checkNotNull(dice)

        val diceHandlerEnv = DiceHandlerEnvironment(bot, update, message, dice)
        handleDice.invoke(diceHandlerEnv)
    }
}
