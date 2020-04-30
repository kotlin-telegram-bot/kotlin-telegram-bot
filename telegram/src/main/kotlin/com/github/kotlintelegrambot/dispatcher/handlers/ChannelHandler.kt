package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.entities.Update

class ChannelHandler(handleUpdate: HandleUpdate) : Handler(handleUpdate) {
    override val groupIdentifier: String
        get() = "channel"

    override fun checkUpdate(update: Update): Boolean {
        return update.channelPost != null || update.editedChannelPost != null
    }
}
