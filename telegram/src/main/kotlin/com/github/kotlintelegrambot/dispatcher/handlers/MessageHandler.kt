package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.extensions.filters.Filter

class MessageHandlerEnvironment(
        override val bot: Bot,
        override val update: Update,
        val message: Message
) : HandlerEnvironment(bot, update)

internal class MessageHandler(
    private val filter: Filter,
    private val handle: HandleMessage
) : Handler() {

    override val groupIdentifier: String
        get() = "MessageHandler"

    override fun checkUpdate(update: Update): Boolean =
        if (update.message == null) {
            false
        } else {
            filter.checkFor(update.message)
        }

    override fun invoke(bot: Bot, update: Update){
        checkNotNull(update.message)
        handle.invoke(MessageHandlerEnvironment(bot, update, update.message))
    }
}
