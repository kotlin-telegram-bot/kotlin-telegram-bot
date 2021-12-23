package com.github.kotlintelegrambot.errors

import com.github.kotlintelegrambot.types.DispatchableObject

public interface TelegramError : DispatchableObject {
    public enum class Error {
        RETRIEVE_UPDATES
    }

    public fun getType(): Error
    public fun getErrorMessage(): String
}
