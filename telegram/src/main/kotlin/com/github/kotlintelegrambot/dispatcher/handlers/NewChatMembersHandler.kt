package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.entities.Update

class NewChatMembersHandler(
    handlerCallback: HandleUpdate
) : Handler(handlerCallback) {
    override val groupIdentifier: String
        get() = "System"

    override fun checkUpdate(update: Update): Boolean {
        return update.message?.newChatMembers != null
    }
}
