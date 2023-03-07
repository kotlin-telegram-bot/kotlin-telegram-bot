package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

data class TextHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val message: Message,
    val text: String
)

internal class TextHandler(
    private val text: String? = null,
    private val handleText: HandleText
) : Handler {

    override fun checkUpdate(update: Update): Boolean {
        if (update.message?.text != null && text == null) return true
        else if (text != null) {
            return update.message?.text != null && update.message.text.contains(text, ignoreCase = true)
        }
        return false
    }

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        checkNotNull(update.message)
        checkNotNull(update.message.text)
        val textHandlerEnv = TextHandlerEnvironment(
            bot,
            update,
            update.message,
            update.message.text
        )
        handleText(textHandlerEnv)
    }
}
