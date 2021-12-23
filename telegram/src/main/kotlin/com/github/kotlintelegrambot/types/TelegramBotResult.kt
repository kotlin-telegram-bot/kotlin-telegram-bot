package com.github.kotlintelegrambot.types

import com.github.kotlintelegrambot.network.Response
import java.lang.Exception

/**
 * Union type representing the execution result of a Telegram Bot operation.
 * It will be either a success [Success] or an error [Error]
 */
public sealed class TelegramBotResult<T> {

    /**
     * Returns True if this a [Success] and False otherwise. Added for flexibility, but the use
     * of [fold] is recommended instead.
     */
    public abstract val isSuccess: Boolean

    /**
     * Returns True if this an [Error] and False otherwise. Added for flexibility, but the use
     * of [fold] is recommended instead.
     */
    public abstract val isError: Boolean

    /**
     * Represents a Telegram Bot Api successful response.
     */
    public data class Success<T>(val value: T) : TelegramBotResult<T>() {

        override val isSuccess: Boolean
            get() = true

        override val isError: Boolean
            get() = false
    }

    public sealed class Error<T> : TelegramBotResult<T>() {

        override val isSuccess: Boolean
            get() = false

        override val isError: Boolean
            get() = true

        /**
         * Represents an HTTP error.
         */
        public data class HttpError<T>(val httpCode: Int, val description: String?) : Error<T>()

        /**
         * Represents a Telegram Bot Api error response.
         */
        public data class TelegramApi<T>(val errorCode: Int, val description: String) : Error<T>()

        /**
         * Represents a response error that can't be mapped to a Telegram Bot Api error response.
         */
        public data class InvalidResponse<T>(
            val httpCode: Int,
            val httpStatusMessage: String?,
            val body: Response<T>?
        ) : Error<T>()

        /**
         * Wraps any exception thrown while executing and processing an api call.
         */
        public data class Unknown<T>(val exception: Exception) : Error<T>()
    }

    /**
     * Returns the [Success] value if available, otherwise null.
     */
    public fun getOrNull(): T? = if (this is Success) value else null

    /**
     * Returns the [Success] value if available, otherwise [default].
     */
    public fun getOrDefault(default: T): T = if (this is Success) value else default

    /**
     * Returns the [Success] value if available, otherwise throws an exception.
     */
    public fun get(): T = if (this is Success) value else error("Can't get success value in $this")

    /**
     * Applies [ifSuccess] if this is a [Success] and [ifError] if this is an [Error].
     *
     * @param ifSuccess the function to apply if this is a [Success].
     * @param ifError the function to apply if this is an [Error].
     *
     * @return the result of applying the correspondent function.
     */
    public suspend inline fun <R> fold(
        crossinline ifSuccess: suspend (T) -> R,
        crossinline ifError: suspend (Error<T>) -> R,
    ): R = when (this) {
        is Success -> ifSuccess(value)
        is Error -> ifError(this)
    }
}
