package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.HandleChatJoinRequest
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.entities.ChatJoinRequest
import com.github.kotlintelegrambot.entities.Update

data class ChatJoinRequestHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val chatJoinRequest: ChatJoinRequest
)

internal class ChatJoinRequestHandler(
    private val handleChatJoinRequest: HandleChatJoinRequest
) : Handler {

    override fun checkUpdate(update: Update) = update.chatJoinRequest != null

    override fun handleUpdate(bot: Bot, update: Update)
    {
        val chatJoinRequest = update.chatJoinRequest
        checkNotNull(chatJoinRequest)

        val joinRequestEnv = ChatJoinRequestHandlerEnvironment(bot, update, chatJoinRequest)
        handleChatJoinRequest(joinRequestEnv)
    }
}
