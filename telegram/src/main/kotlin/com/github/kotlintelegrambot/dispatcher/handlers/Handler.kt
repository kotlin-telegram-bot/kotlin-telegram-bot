package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update

/**
 * [Handler]s are the components in charge of processing bot updates. Usually, you shouldn't
 * implement this interface but rather use the built-in implementations. However, if you need to
 * implement some custom updates handling that is not provided by the library, you can
 * implement it and add the handler to the [Dispatcher] so it receives the updates.
 */
interface Handler {
    /**
     * Whether the handler should process the received [update].
     *
     * @param update Telegram bot update that the handler might want to process.
     *
     * @return True if the handler should process the update, false otherwise.
     */
    fun checkUpdate(update: Update): Boolean

    /**
     * Code to act on a received [Update].
     *
     * @param bot Instance of the Telegram bot that received the update.
     * @param update The update to be processed.
     */
    suspend fun handleUpdate(bot: Bot, update: Update)
}
