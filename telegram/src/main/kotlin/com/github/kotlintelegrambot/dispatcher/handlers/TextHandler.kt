package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

data class TextHandlerEnvironment(
    override val bot: Bot,
    override val update: Update,
    val message: Message,
    val text: String
) : HandlerEnvironment(bot, update)

internal class TextHandler(
    private val text: String? = null,
    private val handleText: HandleText
) : Handler() {
    override val groupIdentifier: String = "CommandHandler"

    override fun checkUpdate(update: Update): Boolean {
        if (update.message?.text != null && text == null) return true
        else if (text != null) {
            return (update.message?.text != null && update.message.text.toLowerCase().contains(text.toLowerCase()))
        }
        return false
    }

    override fun invoke(bot: Bot, update: Update) {
        checkNotNull(update.message)
        checkNotNull(update.message.text)
        handleText.invoke(TextHandlerEnvironment(bot, update, update.message, update.message.text))
    }
}

