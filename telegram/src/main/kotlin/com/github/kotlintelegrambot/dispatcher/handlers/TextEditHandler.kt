package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandler


class TextEditHandler(
    private val text: String? = null,
    private val handleText: HandleText,
) : Handler {

    override fun checkUpdate(update: Update): Boolean {
        if (update.editedMessage?.text != null && text == null) {
            return true
        } else if (text != null) {
            return update.editedMessage?.text != null && update.editedMessage.text.contains(text, ignoreCase = true)
        }
        return false
    }

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        checkNotNull(update.editedMessage)
        checkNotNull(update.editedMessage.text)
        val textHandlerEnv = TextHandlerEnvironment(
            bot,
            update,
            update.editedMessage,
            update.editedMessage.text,
        )
        handleText(textHandlerEnv)
    }
}
