package com.github.kotlintelegrambot.errors

class RetrieveUpdatesError(
    private val errorMessage: String,
) : TelegramError {
    override fun getType(): TelegramError.Error = TelegramError.Error.RETRIEVE_UPDATES

    override fun getErrorMessage(): String = errorMessage
}
