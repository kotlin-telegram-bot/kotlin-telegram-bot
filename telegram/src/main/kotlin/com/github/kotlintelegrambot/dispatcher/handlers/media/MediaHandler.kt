package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.dispatcher.handlers.HandlerEnvironment
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

data class MediaHandlerEnvironment<Media>(
    override val bot: Bot,
    override val update: Update,
    val message: Message,
    val media: Media
) : HandlerEnvironment(bot, update)

internal abstract class MediaHandler<Media>(
    private val handle: MediaHandlerEnvironment<Media>.() -> Unit,
    private val toMedia: Message.() -> Media,
    private val isUpdateMedia: (Update) -> Boolean
) : Handler() {

    override val groupIdentifier: String
        get() = "MediaHandler"

    override fun checkUpdate(update: Update): Boolean = isUpdateMedia(update)

    override fun invoke(bot: Bot, update: Update) {
        checkNotNull(update.message)
        val media = update.message.toMedia()
        handle.invoke(MediaHandlerEnvironment(bot, update, update.message, media))
    }
}
