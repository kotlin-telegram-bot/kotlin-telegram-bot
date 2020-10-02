package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.dice.Dice

data class DiceHandlerEnvironment(
    override val bot: Bot,
    override val update: Update,
    val message: Message,
    val dice: Dice
) : HandlerEnvironment(bot, update)

internal class DiceHandler(private val handle: HandleDice) : Handler() {
    override val groupIdentifier: String
        get() = "dice"

    override fun checkUpdate(update: Update): Boolean = update.message?.dice != null

    override fun invoke(bot: Bot, update: Update) {
        val message = update.message
        val dice = message?.dice
        checkNotNull(dice)
        handle.invoke(DiceHandlerEnvironment(bot, update, message, dice))
    }
}

