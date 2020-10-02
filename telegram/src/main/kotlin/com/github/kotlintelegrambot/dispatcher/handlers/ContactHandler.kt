package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Contact
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

data class ContactHandlerEnvironment(
    override val bot: Bot,
    override val update: Update,
    val message: Message,
    val contact: Contact
): HandlerEnvironment(bot, update)

internal class ContactHandler(
    private val handle: HandleContact
) : Handler() {

    override val groupIdentifier: String
        get() = "System"

    override fun checkUpdate(update: Update): Boolean {
        return update.message?.contact != null
    }

    override fun invoke(bot: Bot, update: Update) {
        checkNotNull(update.message)
        checkNotNull(update.message.contact)
        handle.invoke(ContactHandlerEnvironment(bot, update, update.message, update.message.contact))
    }
}

