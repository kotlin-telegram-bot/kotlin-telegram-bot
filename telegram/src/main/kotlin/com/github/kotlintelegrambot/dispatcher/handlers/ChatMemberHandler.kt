package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatMemberUpdated
import com.github.kotlintelegrambot.entities.Update

data class ChatMemberHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val chatMember: ChatMemberUpdated,
)

class ChatMemberHandler(
    private val handleChatMember: HandleChatMember,
) : Handler {
    override fun checkUpdate(update: Update): Boolean = update.chatMember != null
    override suspend fun handleUpdate(bot: Bot, update: Update) {
        checkNotNull(update.chatMember)
        handleChatMember(ChatMemberHandlerEnvironment(bot, update, update.chatMember))
    }
}
