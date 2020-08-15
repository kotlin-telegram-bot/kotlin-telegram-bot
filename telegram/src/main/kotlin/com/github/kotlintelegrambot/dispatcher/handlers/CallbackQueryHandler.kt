package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.Update

data class CallbackQueryHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val callbackQuery: CallbackQuery
)

internal class CallbackQueryHandler(
    private val callbackData: String? = null,
    callbackAnswerText: String? = null,
    callbackAnswerShowAlert: Boolean? = null,
    callbackAnswerUrl: String? = null,
    callbackAnswerCacheTime: Int? = null,
    handleCallbackQuery: HandleCallbackQuery
) :
    Handler(
        CallbackQueryHandlerProxy(
            handleCallbackQuery,
            callbackAnswerText,
            callbackAnswerShowAlert,
            callbackAnswerUrl,
            callbackAnswerCacheTime
        )
    ) {

    override val groupIdentifier: String = "CallbackQuery"

    override fun checkUpdate(update: Update): Boolean {
        val data = update.callbackQuery?.data
        return when {
            data == null -> false
            callbackData == null -> true
            else -> data.toLowerCase().contains(callbackData.toLowerCase())
        }
    }
}

private class CallbackQueryHandlerProxy(
    private val handleCallbackQuery: HandleCallbackQuery,
    private val text: String? = null,
    private val showAlert: Boolean? = null,
    private val url: String? = null,
    private val cacheTime: Int? = null
) : HandleUpdate {

    override fun invoke(bot: Bot, update: Update) {
        checkNotNull(update.callbackQuery)
        val callbackQueryHandlerEnv = CallbackQueryHandlerEnvironment(
            bot,
            update,
            update.callbackQuery
        )
        handleCallbackQuery(callbackQueryHandlerEnv)

        val callbackQueryId = update.callbackQuery.id
        bot.answerCallbackQuery(
            callbackQueryId = callbackQueryId,
            text = text,
            showAlert = showAlert,
            url = url,
            cacheTime = cacheTime
        )
    }
}
