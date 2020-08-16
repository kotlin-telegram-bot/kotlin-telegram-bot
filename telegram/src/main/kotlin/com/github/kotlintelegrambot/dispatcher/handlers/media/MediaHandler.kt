package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.HandleUpdate
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

data class MediaHandlerEnvironment<Media>(
    val bot: Bot,
    val update: Update,
    val message: Message,
    val media: Media
)

internal abstract class MediaHandler<Media>(
    handleMediaUpdate: MediaHandlerEnvironment<Media>.() -> Unit,
    toMedia: Message.() -> Media,
    private val isUpdateMedia: (Update) -> Boolean
) : Handler(MediaHandlerProxy(handleMediaUpdate, toMedia)) {

    override val groupIdentifier: String
        get() = "MediaHandler"

    override fun checkUpdate(update: Update): Boolean = isUpdateMedia(update)
}

private class MediaHandlerProxy<Media>(
    private val handleMediaUpdate: MediaHandlerEnvironment<Media>.() -> Unit,
    private val toMedia: Message.() -> Media
) : HandleUpdate {

    override fun invoke(bot: Bot, update: Update) {
        checkNotNull(update.message)
        val media = update.message.toMedia()
        val mediaHandlerEnvironment = MediaHandlerEnvironment(bot, update, update.message, media)
        handleMediaUpdate.invoke(mediaHandlerEnvironment)
    }
}
