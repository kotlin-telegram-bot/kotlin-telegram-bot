package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.entities.Update

class CallbackQueryHandler(
    private val callbackData: String? = null,
    callbackAnswerText: String? = null,
    callbackAnswerShowAlert: Boolean? = null,
    callbackAnswerUrl: String? = null,
    callbackAnswerCacheTime: Int? = null,
    handler: HandleUpdate
) :
    Handler(
        HandleUpdateWrapper(
            handler,
            callbackAnswerText,
            callbackAnswerShowAlert,
            callbackAnswerUrl,
            callbackAnswerCacheTime
        )
    ) {

    override val groupIdentifier: String = "CallbackQuery"

    override fun checkUpdate(update: Update): Boolean {
        val data = update.callbackQuery?.data
        return if (data != null && callbackData == null) {
            true
        } else if (callbackData != null) {
            (data != null && data.toLowerCase().contains(callbackData.toLowerCase()))
        } else {
            false
        }
    }

    private class HandleUpdateWrapper(
        private val handleUpdate: HandleUpdate,
        private val text: String? = null,
        private val showAlert: Boolean? = null,
        private val url: String? = null,
        private val cacheTime: Int? = null
    ) : HandleUpdate {
        override fun invoke(bot: Bot, updte: Update) {
            handleUpdate(bot, updte)

            val callbackQuery = updte.callbackQuery ?: return
            val callbackQueryId = callbackQuery.id

            bot.answerCallbackQuery(
                callbackQueryId = callbackQueryId,
                text = text,
                showAlert = showAlert,
                url = url,
                cacheTime = cacheTime
            )
        }
    }
}
