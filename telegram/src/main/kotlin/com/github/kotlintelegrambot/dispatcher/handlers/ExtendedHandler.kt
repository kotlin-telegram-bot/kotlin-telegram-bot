package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.entities.Update

class ExtendedHandler(
    private val delegateHandler: Handler,
    private val predicate: (Update) -> Boolean,
) : Handler by delegateHandler {

    override fun checkUpdate(update: Update): Boolean =
        delegateHandler.checkUpdate(update) && predicate(update)
}
