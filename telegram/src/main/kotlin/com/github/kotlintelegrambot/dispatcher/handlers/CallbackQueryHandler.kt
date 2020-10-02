package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.Update

class CallbackQueryHandlerEnvironment(
    override val bot: Bot,
    override val update: Update,
    val callbackQuery: CallbackQuery
) : HandlerEnvironment(bot, update)

internal class CallbackQueryHandler(
    private val callbackData: String? = null,
    private val callbackAnswerText: String? = null,
    private val callbackAnswerShowAlert: Boolean? = null,
    private val callbackAnswerUrl: String? = null,
    private val callbackAnswerCacheTime: Int? = null,
    private val handleCallbackQuery: HandleCallbackQuery
) : Handler() {

    override val groupIdentifier: String = "CallbackQuery"

    override fun checkUpdate(update: Update): Boolean {
        val data = update.callbackQuery?.data
        return when {
            data == null -> false
            callbackData == null -> true
            else -> data.toLowerCase().contains(callbackData.toLowerCase())
        }
    }

    override fun invoke(bot: Bot, update: Update) {
        checkNotNull(update.callbackQuery)
        handleCallbackQuery.invoke(CallbackQueryHandlerEnvironment(bot, update, update.callbackQuery))
        bot.answerCallbackQuery(
            callbackQueryId = update.callbackQuery.id,
            text = callbackAnswerText,
            showAlert = callbackAnswerShowAlert,
            url = callbackAnswerUrl,
            cacheTime = callbackAnswerCacheTime
        )
    }
}

