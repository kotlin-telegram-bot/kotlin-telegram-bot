package me.ivmg.telegram.dispatcher.handlers

import me.ivmg.telegram.HandleUpdate
import me.ivmg.telegram.dispatcher.filters.Filter
import me.ivmg.telegram.entities.Update

class InvoiceHandler(filter: Filter? = null, handleUpdate: HandleUpdate) : Handler(handleUpdate, filter) {
    override val groupIdentifier: String
        get() = "payment"

    override fun checkUpdate(update: Update): Boolean {
        return update.message != null
    }
}

class CheckoutHandler(filter: Filter? = null, handleUpdate: HandleUpdate) : Handler(handleUpdate, filter) {
    override val groupIdentifier: String
        get() = "payment"

    override fun checkUpdate(update: Update): Boolean {
        return update.preCheckoutQuery != null
    }
}