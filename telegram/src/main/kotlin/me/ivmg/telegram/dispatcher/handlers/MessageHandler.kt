package me.ivmg.telegram.dispatcher.handlers

import me.ivmg.telegram.HandleUpdate
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.extensions.filters.Filter

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
