package me.ivmg.telegram.dispatcher.handlers

import me.ivmg.telegram.Bot
import me.ivmg.telegram.HandleUpdate
import me.ivmg.telegram.LocationHandleUpdate
import me.ivmg.telegram.dispatcher.filters.Filter
import me.ivmg.telegram.entities.Update

class LocationHandler(filter: Filter? = null, handleUpdate: LocationHandleUpdate) : Handler(LocationHandleUpdateProxy(handleUpdate), filter) {
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