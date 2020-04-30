package com.github.kotlintelegrambot.dispatcher.handlers.media

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.dispatcher.handlers.Handler
import com.github.kotlintelegrambot.entities.Update

abstract class MediaHandler<T>(
    handleMediaUpdate: (Bot, Update, T) -> Unit,
    toMedia: (Update) -> T,
    private val predicate: (Update) -> Boolean
) : Handler(MediaHandlerProxy(handleMediaUpdate, toMedia)) {

    override val groupIdentifier: String
        get() = "MediaHandler"

    override fun checkUpdate(update: Update): Boolean = predicate(update)
}

private class MediaHandlerProxy<T>(
    private val handleMediaUpdate: (Bot, Update, T) -> Unit,
    private val toMedia: Update.() -> T
) : HandleUpdate {

    override fun invoke(bot: Bot, update: Update) {
        handleMediaUpdate(bot, update, update.toMedia())
    }
}
