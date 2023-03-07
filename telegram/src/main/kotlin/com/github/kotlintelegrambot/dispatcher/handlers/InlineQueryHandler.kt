package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.InlineQuery
import com.github.kotlintelegrambot.entities.Update

data class InlineQueryHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val inlineQuery: InlineQuery
)

internal class InlineQueryHandler(
    private val handleInlineQuery: HandleInlineQuery
) : Handler {

    override fun checkUpdate(update: Update): Boolean = update.inlineQuery != null

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        val inlineQuery = update.inlineQuery
        checkNotNull(inlineQuery)

        val inlineQueryHandlerEnv = InlineQueryHandlerEnvironment(bot, update, inlineQuery)
        handleInlineQuery(inlineQueryHandlerEnv)
    }
}
