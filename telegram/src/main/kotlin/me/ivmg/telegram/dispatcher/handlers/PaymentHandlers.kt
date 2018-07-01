package me.ivmg.telegram.dispatcher.handlers

import me.ivmg.telegram.HandleUpdate
import me.ivmg.telegram.entities.Update

class InvoiceHandler(handleUpdate: HandleUpdate) : Handler(handleUpdate) {
    override val groupIdentifier: String
        get() = "payment"

    override fun checkUpdate(update: Update): Boolean {
        return update.message != null
    }
}

class CheckoutHandler(handleUpdate: HandleUpdate) : Handler(handleUpdate) {
    override val groupIdentifier: String
        get() = "payment"

    override fun checkUpdate(update: Update): Boolean {
        return update.preCheckoutQuery != null
    }
}