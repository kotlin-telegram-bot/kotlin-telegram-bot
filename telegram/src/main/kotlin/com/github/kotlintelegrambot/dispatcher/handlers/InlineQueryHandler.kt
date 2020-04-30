package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.HandleInlineQuery
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.entities.Update

class InlineQueryHandler(
    handleInlineQuery: HandleInlineQuery
) : Handler(InlineQueryHandlerProxy(handleInlineQuery)) {
    override val groupIdentifier: String
        get() = "InlineQueryHandler"

    override fun checkUpdate(update: Update): Boolean = update.inlineQuery != null
}

private class InlineQueryHandlerProxy(
    private val handleInlineQuery: HandleInlineQuery
) : HandleUpdate {

    override fun invoke(bot: Bot, update: Update) {
        val inlineQuery = update.inlineQuery
        checkNotNull(inlineQuery)
        handleInlineQuery(bot, inlineQuery)
    }
}
