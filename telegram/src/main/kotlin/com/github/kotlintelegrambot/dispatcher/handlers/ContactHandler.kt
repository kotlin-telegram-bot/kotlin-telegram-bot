package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.ContactHandleUpdate
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.entities.Update

class ContactHandler(handleUpdate: ContactHandleUpdate) : Handler(ContactHandleUpdateProxy(handleUpdate)) {

    override val groupIdentifier: String
        get() = "System"

    override fun checkUpdate(update: Update): Boolean {
        return update.message?.contact != null
    }
}

private class ContactHandleUpdateProxy(private val handleUpdate: ContactHandleUpdate) : HandleUpdate {
    override fun invoke(bot: Bot, update: Update) {
        handleUpdate(bot, update, update.message?.contact!!)
    }
}
