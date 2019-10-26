package me.ivmg.telegram.dispatcher.handlers

import me.ivmg.telegram.Bot
import me.ivmg.telegram.HandleInlineQuery
import me.ivmg.telegram.HandleUpdate
import me.ivmg.telegram.entities.Update

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
