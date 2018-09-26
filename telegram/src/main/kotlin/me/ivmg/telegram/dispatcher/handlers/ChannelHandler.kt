package me.ivmg.telegram.dispatcher.handlers

import me.ivmg.telegram.HandleUpdate
import me.ivmg.telegram.entities.Update

class ChannelHandler(handleUpdate: HandleUpdate) : Handler(handleUpdate) {
    override val groupIdentifier: String
        get() = "channel"

    override fun checkUpdate(update: Update): Boolean {
        return update.channelPost != null || update.editedChannelPost != null
    }
}
