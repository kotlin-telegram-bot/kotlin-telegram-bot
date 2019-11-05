package me.ivmg.telegram.dispatcher.handlers.media

import me.ivmg.telegram.Bot
import me.ivmg.telegram.HandleUpdate
import me.ivmg.telegram.dispatcher.handlers.Handler
import me.ivmg.telegram.entities.Update

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
