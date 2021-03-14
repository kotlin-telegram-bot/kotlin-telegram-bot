package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Contact
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

data class ContactHandlerEnvironment(
    override val bot: Bot,
    override val update: Update,
    override val message: Message,
    val contact: Contact
): IMessageHandlerEnvironment

internal class ContactHandler(
    private val handleContact: HandleContact
) : Handler {

    override fun checkUpdate(update: Update): Boolean {
        return update.message?.contact != null
    }

    override fun handleUpdate(bot: Bot, update: Update) {
        checkNotNull(update.message)
        checkNotNull(update.message.contact)

        val contactHandlerEnv = ContactHandlerEnvironment(
            bot,
            update,
            update.message,
            update.message.contact
        )
        handleContact(contactHandlerEnv)
    }
}
