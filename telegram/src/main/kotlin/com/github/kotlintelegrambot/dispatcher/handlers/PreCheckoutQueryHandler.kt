package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.payments.PreCheckoutQuery

data class PreCheckoutQueryHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val preCheckoutQuery: PreCheckoutQuery
)

internal class PreCheckoutQueryHandler(
    handlePreCheckoutQuery: HandlePreCheckoutQuery
) : Handler(PreCheckoutQueryHandlerProxy(handlePreCheckoutQuery)) {

    override val groupIdentifier: String
        get() = "payment"

    override fun checkUpdate(update: Update): Boolean {
        return update.preCheckoutQuery != null
    }
}

private class PreCheckoutQueryHandlerProxy(
    private val handlePreCheckoutQuery: HandlePreCheckoutQuery
) : HandleUpdate {

    override fun invoke(bot: Bot, update: Update) {
        checkNotNull(update.preCheckoutQuery)

        val preCheckoutQueryHandlerEnv = PreCheckoutQueryHandlerEnvironment(
            bot,
            update,
            update.preCheckoutQuery
        )
        handlePreCheckoutQuery(preCheckoutQueryHandlerEnv)
    }
}
