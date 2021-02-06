package com.github.kotlintelegrambot.types

import com.github.kotlintelegrambot.network.Response
import java.lang.Exception

/**
 * Union type representing the execution result of a Telegram Bot operation.
 * It will be either a success [Success] or an error [Error]
 */
sealed class TelegramBotResult<T> {

    /**
     * Represents a Telegram Bot Api successful response.
     */
    data class Success<T>(val value: T) : TelegramBotResult<T>()

    sealed class Error<T> : TelegramBotResult<T>() {

        /**
         * Represents an HTTP error.
         */
        data class HttpError<T>(val httpCode: Int, val description: String?) : Error<T>()

        /**
         * Represents a Telegram Bot Api error response.
         */
        data class TelegramApi<T>(val errorCode: Int, val description: String) : Error<T>()

        /**
         * Represents a response error that can't be mapped to a Telegram Bot Api error response.
         */
        data class InvalidResponse<T>(
            val httpCode: Int,
            val httpStatusMessage: String?,
            val body: Response<T>?
        ) : Error<T>()

        /**
         * Wraps any exception thrown while executing and processing an api call.
         */
        data class Unknown<T>(val exception: Exception) : Error<T>()
    }

    /**
     * Returns the [Success] value if available, otherwise null.
     */
    fun getOrNull(): T? = if (this is Success) value else null
}
