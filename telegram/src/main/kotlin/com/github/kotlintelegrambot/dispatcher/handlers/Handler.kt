package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.entities.Update

abstract class Handler(val handlerCallback: HandleUpdate) {
    abstract val groupIdentifier: String

    abstract fun checkUpdate(update: Update): Boolean
}
