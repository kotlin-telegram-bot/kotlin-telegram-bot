package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Location
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

data class LocationHandlerEnvironment(
    override val bot: Bot,
    override val update: Update,
    val message: Message,
    val location: Location
) : HandlerEnvironment(bot, update)

internal class LocationHandler(
    private val handle: HandleLocation
) : Handler() {
    override val groupIdentifier: String
        get() = "System"

    override fun checkUpdate(update: Update): Boolean {
        return update.message?.location != null
    }

    override fun invoke(bot: Bot, update: Update) {
        checkNotNull(update.message)
        checkNotNull(update.message.location)
        handle.invoke(LocationHandlerEnvironment(bot, update, update.message, update.message.location))
    }
}

