package com.github.kotlintelegrambot.errors

class RetrieveUpdatesError(private val serverResponse: String) : TelegramError {
    override fun getType(): TelegramError.Error {
        return TelegramError.Error.RETRIEVE_UPDATES
    }

    override fun getErrorMessage(): String {
        return "Server response: {$serverResponse}"
    }
}
