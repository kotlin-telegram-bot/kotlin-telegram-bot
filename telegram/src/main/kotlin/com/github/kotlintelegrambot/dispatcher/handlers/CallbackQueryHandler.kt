package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.Update

data class CallbackQueryHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val callbackQuery: CallbackQuery,
)

class CallbackQueryHandler(
    private val callbackData: String? = null,
    private val startsWith: String? = null,
    private val callbackAnswerText: String? = null,
    private val callbackAnswerShowAlert: Boolean? = null,
    private val callbackAnswerUrl: String? = null,
    private val callbackAnswerCacheTime: Int? = null,
    private val handleCallbackQuery: HandleCallbackQuery,
) : Handler {

    override fun checkUpdate(update: Update): Boolean {
        val data = update.callbackQuery?.data
        return when {
            data == null -> false
            startsWith != null -> data.startsWith(startsWith, ignoreCase = true)
            callbackData != null -> data.contains(callbackData, ignoreCase = true)
            else -> true
        }
    }

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        checkNotNull(update.callbackQuery)
        val callbackQueryHandlerEnv = CallbackQueryHandlerEnvironment(
            bot,
            update,
            update.callbackQuery,
        )
        handleCallbackQuery(callbackQueryHandlerEnv)

        val callbackQueryId = update.callbackQuery.id
        bot.answerCallbackQuery(
            callbackQueryId = callbackQueryId,
            text = callbackAnswerText,
            showAlert = callbackAnswerShowAlert,
            url = callbackAnswerUrl,
            cacheTime = callbackAnswerCacheTime,
        )
    }
}
