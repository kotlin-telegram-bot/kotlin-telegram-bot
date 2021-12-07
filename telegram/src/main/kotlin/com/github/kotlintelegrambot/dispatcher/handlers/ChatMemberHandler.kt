package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatMemberUpdated
import com.github.kotlintelegrambot.entities.Update

data class ChatMemberHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val chatMember: ChatMemberUpdated
)

internal class ChatMemberHandler(
    private val handleChatMember: HandleChatMember
) : Handler {

    override fun checkUpdate(update: Update) = update.chatMember != null

    override fun handleUpdate(bot: Bot, update: Update)
    {
        val chatMember = update.chatMember
        checkNotNull(chatMember)

        val chatMemberEnv = ChatMemberHandlerEnvironment(bot, update, chatMember)
        handleChatMember(chatMemberEnv)
    }
}
