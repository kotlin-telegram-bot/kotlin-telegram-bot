package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.extensions.filters.Filter

class MessageHandler(
    handlerCallback: HandleUpdate,
    private val filter: Filter
) : Handler(handlerCallback) {

    override val groupIdentifier: String
        get() = "MessageHandler"

    override fun checkUpdate(update: Update): Boolean =
        if (update.message == null) {
            false
        } else {
            filter.checkFor(update.message)
        }
}
