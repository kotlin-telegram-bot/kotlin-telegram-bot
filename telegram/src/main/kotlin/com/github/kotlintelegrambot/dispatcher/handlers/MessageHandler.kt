package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.extensions.filters.Filter

data class MessageHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val message: Message,
    val isEdited: Boolean
)

internal class MessageHandler(
    private val filter: Filter,
    private val handleMessage: MessageHandlerEnvironment.() -> Unit
) : Handler {

    override fun checkUpdate(update: Update): Boolean =
        when {
            update.message != null -> filter.checkFor(update.message)
            update.editedMessage != null -> filter.checkFor(update.editedMessage)
            else -> false
        }

    override fun handleUpdate(bot: Bot, update: Update) {
        val isEdited: Boolean
        val message = when {
            update.message != null -> {
                isEdited = false
                update.message
            }
            update.editedMessage != null -> {
                isEdited = true
                update.editedMessage
            }
            else -> {
                isEdited = false
                null
            }
        }

        checkNotNull(message)
        val messageHandlerEnv = MessageHandlerEnvironment(bot, update, message, isEdited)
        handleMessage(messageHandlerEnv)
    }
}
