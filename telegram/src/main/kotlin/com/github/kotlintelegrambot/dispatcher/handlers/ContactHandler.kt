package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Contact
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

data class ContactHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val message: Message,
    val contact: Contact
)

internal class ContactHandler(
    handleContact: HandleContact
) : Handler(ContactHandlerProxy(handleContact)) {

    override val groupIdentifier: String
        get() = "System"

    override fun checkUpdate(update: Update): Boolean {
        return update.message?.contact != null
    }
}

private class ContactHandlerProxy(
    private val handler: HandleContact
) : HandleUpdate {

    override fun invoke(bot: Bot, update: Update) {
        checkNotNull(update.message)
        checkNotNull(update.message.contact)

        val contactHandlerEnv = ContactHandlerEnvironment(
            bot,
            update,
            update.message,
            update.message.contact
        )
        handler.invoke(contactHandlerEnv)
    }
}
