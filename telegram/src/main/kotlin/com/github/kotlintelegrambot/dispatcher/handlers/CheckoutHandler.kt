package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.entities.Update

class CheckoutHandler(handleUpdate: HandleUpdate) : Handler(handleUpdate) {
    override val groupIdentifier: String
        get() = "payment"

    override fun checkUpdate(update: Update): Boolean {
        return update.preCheckoutQuery != null
    }
}
