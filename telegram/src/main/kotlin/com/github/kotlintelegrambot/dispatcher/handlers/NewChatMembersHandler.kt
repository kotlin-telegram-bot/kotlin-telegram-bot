package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.User

data class NewChatMembersHandlerEnvironment(
    override val bot: Bot,
    override val update: Update,
    val message: Message,
    val newChatMembers: List<User>
) : HandlerEnvironment(bot, update)

internal class NewChatMembersHandler(
    private val handle: HandleNewChatMembers
) : Handler() {
    override val groupIdentifier: String
        get() = "newChatMembers"

    override fun checkUpdate(update: Update): Boolean {
        val newChatMembers = update.message?.newChatMembers
        return newChatMembers != null && newChatMembers.isNotEmpty()
    }

    override fun invoke(bot: Bot, update: Update) {
        val message = update.message
        val newChatMembers = message?.newChatMembers
        checkNotNull(newChatMembers)
        handle.invoke(NewChatMembersHandlerEnvironment(bot, update, message, newChatMembers))
    }
}