package com.github.kotlintelegrambot.errors

import com.github.kotlintelegrambot.types.DispatchableObject

interface TelegramError : DispatchableObject {
    enum class Error {
        RETRIEVE_UPDATES
    }

    fun getType(): Error
    fun getErrorMessage(): String
}
