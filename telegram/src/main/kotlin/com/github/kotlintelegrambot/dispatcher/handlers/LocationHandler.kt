package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.LocationHandleUpdate
import com.github.kotlintelegrambot.entities.Update

class LocationHandler(handleUpdate: LocationHandleUpdate) : Handler(LocationHandleUpdateProxy(handleUpdate)) {
    override val groupIdentifier: String
        get() = "System"

    override fun checkUpdate(update: Update): Boolean {
        return update.message?.location != null
    }
}

private class LocationHandleUpdateProxy(private val handleUpdate: LocationHandleUpdate) : HandleUpdate {
    override fun invoke(bot: Bot, update: Update) {
        handleUpdate(bot, update, update.message?.location!!)
    }
}
