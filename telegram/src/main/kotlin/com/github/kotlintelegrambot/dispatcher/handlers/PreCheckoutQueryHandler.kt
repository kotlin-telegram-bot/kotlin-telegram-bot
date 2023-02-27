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
    private val handlePreCheckoutQuery: HandlePreCheckoutQuery
) : Handler {

    override fun checkUpdate(update: Update): Boolean {
        return update.preCheckoutQuery != null
    }

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        checkNotNull(update.preCheckoutQuery)

        val preCheckoutQueryHandlerEnv = PreCheckoutQueryHandlerEnvironment(
            bot,
            update,
            update.preCheckoutQuery
        )
        handlePreCheckoutQuery(preCheckoutQueryHandlerEnv)
    }
}
