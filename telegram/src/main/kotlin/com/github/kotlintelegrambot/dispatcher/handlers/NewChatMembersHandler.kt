package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.HandleNewChatMembers
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.entities.Update

class NewChatMembersHandler(
    handleNewChatMembers: HandleNewChatMembers
) : Handler(NewChatMembersHandlerProxy(handleNewChatMembers)) {
    override val groupIdentifier: String
        get() = "newChatMembers"

    override fun checkUpdate(update: Update): Boolean {
        return update.message?.newChatMembers != null
    }
}

private class NewChatMembersHandlerProxy(
    val handleNewChatMembers: HandleNewChatMembers
) : HandleUpdate {
    override fun invoke(bot: Bot, update: Update) {
        val message = update.message
        val newChatMembers = message?.newChatMembers
        checkNotNull(newChatMembers)
        handleNewChatMembers.invoke(bot, message, newChatMembers)
    }
}
