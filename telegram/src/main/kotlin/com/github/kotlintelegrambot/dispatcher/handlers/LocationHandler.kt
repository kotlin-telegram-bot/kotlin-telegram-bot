package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Location
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

data class LocationHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val message: Message,
    val location: Location
)

internal class LocationHandler(
    handleLocation: HandleLocation
) : Handler(LocationHandlerProxy(handleLocation)) {
    override val groupIdentifier: String
        get() = "System"

    override fun checkUpdate(update: Update): Boolean {
        return update.message?.location != null
    }
}

private class LocationHandlerProxy(
    private val handler: HandleLocation
) : HandleUpdate {
    override fun invoke(bot: Bot, update: Update) {
        checkNotNull(update.message)
        checkNotNull(update.message.location)

        val locationHandlerEnv = LocationHandlerEnvironment(
            bot,
            update,
            update.message,
            update.message.location
        )
        handler.invoke(locationHandlerEnv)
    }
}
