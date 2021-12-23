package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.errors.TelegramError

public data class ErrorHandlerEnvironment(
    override val bot: Bot,
    val error: TelegramError
) : HandlerEnvironment

public class ErrorHandler(private val handler: HandleError) {

    public suspend operator fun invoke(bot: Bot, error: TelegramError) {
        val errorHandlerEnvironment = ErrorHandlerEnvironment(bot, error)
        handler.invoke(errorHandlerEnvironment)
    }
}
