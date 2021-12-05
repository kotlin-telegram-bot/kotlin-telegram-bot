package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatJoinRequest
import com.github.kotlintelegrambot.entities.Update

data class ChatJoinRequestHandlerEnvironment internal constructor(
    val bot: Bot,
    val update: Update,
    val request: ChatJoinRequest
)

class ChatJoinRequestHandler(
    private val handleChatJoinRequest: HandleChatJoinRequest
) : Handler {
    override fun checkUpdate(update: Update): Boolean {
        return update.chatJoinRequest != null
    }

    override fun handleUpdate(bot: Bot, update: Update) {
        val request = update.chatJoinRequest
        checkNotNull(request)

        val newChatMembersHandlerEnv = ChatJoinRequestHandlerEnvironment(
            bot,
            update,
            request
        )
        handleChatJoinRequest.invoke(newChatMembersHandlerEnv)
    }
}
