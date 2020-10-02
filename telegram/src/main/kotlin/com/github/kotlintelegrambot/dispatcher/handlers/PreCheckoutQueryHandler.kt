package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.payments.PreCheckoutQuery

data class PreCheckoutQueryHandlerEnvironment(
    override val bot: Bot,
    override val update: Update,
    val preCheckoutQuery: PreCheckoutQuery
) : HandlerEnvironment(bot, update)

internal class PreCheckoutQueryHandler(
    private val handle: HandlePreCheckoutQuery
) : Handler() {

    override val groupIdentifier: String
        get() = "payment"

    override fun checkUpdate(update: Update): Boolean {
        return update.preCheckoutQuery != null
    }

    override fun invoke(bot: Bot, update: Update) {
        checkNotNull(update.preCheckoutQuery)
        handle.invoke(PreCheckoutQueryHandlerEnvironment(bot, update, update.preCheckoutQuery))
    }
}

