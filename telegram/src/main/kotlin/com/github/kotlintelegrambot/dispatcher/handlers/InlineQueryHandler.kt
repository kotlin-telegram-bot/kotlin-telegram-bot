package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.InlineQuery
import com.github.kotlintelegrambot.entities.Update

data class InlineQueryHandlerEnvironment(
    override val bot: Bot,
    override val update: Update,
    val inlineQuery: InlineQuery
) : HandlerEnvironment(bot, update)

internal class InlineQueryHandler(
    private val handle: HandleInlineQuery
) : Handler() {
    override val groupIdentifier: String
        get() = "InlineQueryHandler"

    override fun checkUpdate(update: Update): Boolean = update.inlineQuery != null

    override fun invoke(bot: Bot, update: Update) {
        val inlineQuery = update.inlineQuery
        checkNotNull(inlineQuery)
        handle.invoke(InlineQueryHandlerEnvironment(bot, update, inlineQuery))
    }
}

