package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

data class ChannelHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val channelPost: Message,
    val isEdition: Boolean
)

internal class ChannelHandler(
    private val handleChannelPost: HandleChannelPost
) : Handler {

    override fun checkUpdate(update: Update): Boolean {
        return update.channelPost != null || update.editedChannelPost != null
    }

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        val channelHandlerEnv = when {
            update.channelPost != null -> ChannelHandlerEnvironment(
                bot,
                update,
                update.channelPost,
                isEdition = false
            )
            update.editedChannelPost != null -> ChannelHandlerEnvironment(
                bot,
                update,
                update.editedChannelPost,
                isEdition = true
            )
            else -> error("This method must only be invoked when there is any type of channel post.")
        }

        handleChannelPost.invoke(channelHandlerEnv)
    }
}
