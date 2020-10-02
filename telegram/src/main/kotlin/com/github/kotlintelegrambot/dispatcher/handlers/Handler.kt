package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update

open class HandlerEnvironment (open val bot: Bot, open val update: Update)

abstract class Handler() {
    abstract val groupIdentifier: String
    abstract fun checkUpdate(update: Update): Boolean
    abstract operator fun invoke(bot: Bot, update: Update)
}
