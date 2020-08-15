package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.errors.TelegramError

data class ErrorHandlerEnvironment(
    val bot: Bot,
    val error: TelegramError
)

class ErrorHandler(private val handler: HandleError) {

    operator fun invoke(bot: Bot, error: TelegramError) {
        val errorHandlerEnvironment = ErrorHandlerEnvironment(bot, error)
        handler.invoke(errorHandlerEnvironment)
    }
}
