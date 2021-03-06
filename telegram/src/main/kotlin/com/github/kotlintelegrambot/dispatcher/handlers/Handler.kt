package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.entities.Update

abstract class Handler(val handlerCallback: HandleUpdate) {
    abstract fun checkUpdate(update: Update): Boolean
}
