package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.extensions.filters.Filter

data class MessageHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val message: Message
)

internal class MessageHandler(
    private val filter: Filter,
    private val handleMessage: suspend MessageHandlerEnvironment.() -> Unit
) : Handler {

    override fun checkUpdate(update: Update): Boolean =
        if (update.message == null) {
            false
        } else {
            filter.checkFor(update.message)
        }

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        checkNotNull(update.message)
        val messageHandlerEnv = MessageHandlerEnvironment(bot, update, update.message)
        handleMessage(messageHandlerEnv)
    }
}
