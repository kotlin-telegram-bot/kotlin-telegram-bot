package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

data class CommandHandlerEnvironment internal constructor(
    override val bot: Bot,
    override val update: Update,
    val message: Message,
    val args: List<String>
) : HandlerEnvironment(bot, update)

internal class CommandHandler(
    private val command: String,
    private val handle: HandleCommand
) : Handler() {

    override val groupIdentifier: String = "CommandHandler"

    override fun checkUpdate(update: Update): Boolean {
        return (update.message?.text != null && update.message.text.startsWith("/") &&
            update.message.text.drop(1).split(" ")[0].split("@")[0] == command)
    }

    override fun invoke(bot: Bot, update: Update) {
        checkNotNull(update.message)
        handle.invoke(CommandHandlerEnvironment(bot, update, update.message, update.getCommandArgs()))
    }

    private fun Update.getCommandArgs(): List<String> =
        message?.text?.split("\\s+".toRegex())?.drop(1) ?: emptyList()
}


