package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

data class ChannelHandlerEnvironment(
        override val bot: Bot,
        override val update: Update,
        val channelPost: Message,
        val isEdition: Boolean
): HandlerEnvironment(bot, update)


internal class ChannelHandler(
    private val handle: HandleChannelPost
) : Handler() {

    override val groupIdentifier: String
        get() = "channel"

    override fun checkUpdate(update: Update): Boolean {
        return update.channelPost != null || update.editedChannelPost != null
    }

    override fun invoke(bot: Bot, update: Update) {
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

        handle.invoke(channelHandlerEnv)
    }
}

