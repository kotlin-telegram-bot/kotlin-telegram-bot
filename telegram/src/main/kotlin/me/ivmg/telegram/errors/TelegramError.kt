package me.ivmg.telegram.errors

import me.ivmg.telegram.types.DispatchableObject

interface TelegramError : DispatchableObject {
    enum class Error {
        RETRIEVE_UPDATES
    }

    fun getType(): Error
    fun getErrorMessage(): String
}