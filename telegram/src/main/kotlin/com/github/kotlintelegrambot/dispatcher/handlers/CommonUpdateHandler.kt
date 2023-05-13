package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update

data class CommonHandlerEnvironment internal constructor(
    val bot: Bot,
    val update: Update
)

internal class CommonUpdateHandler(
    private val handleUpdate: HandleUpdate,
) : Handler {
    override fun checkUpdate(update: Update) = true

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        handleUpdate(CommonHandlerEnvironment(bot, update))
    }
}
