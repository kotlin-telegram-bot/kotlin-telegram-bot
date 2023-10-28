package com.github.kotlintelegrambot.types

import com.github.kotlintelegrambot.network.Response
import com.github.kotlintelegrambot.types.TelegramBotResult.Success
import java.lang.Exception

/**
 * Union type representing the execution result of a Telegram Bot operation.
 * It will be either a success [Success] or an error [Error]
 */
sealed class TelegramBotResult<out T : Any> {

    /**
     * Returns True if this a [Success] and False otherwise. Added for flexibility, but the use
     * of [fold] is recommended instead.
     */
    val isSuccess: Boolean
        inline get() = this is Success

    /**
     * Returns True if this an [Error] and False otherwise. Added for flexibility, but the use
     * of [fold] is recommended instead.
     */
    val isError: Boolean
        inline get() = this !is Success

    /**
     * Represents a Telegram Bot Api successful response.
     */
    data class Success<out T : Any>(val value: T) : TelegramBotResult<T>()

    sealed class Error : TelegramBotResult<Nothing>() {

        /**
         * Represents an HTTP error.
         */
        data class HttpError(val httpCode: Int, val description: String?) : Error()

        /**
         * Represents a Telegram Bot Api error response.
         */
        data class TelegramApi(val errorCode: Int, val description: String) : Error()

        /**
         * Represents a response error that can't be mapped to a Telegram Bot Api error response.
         */
        data class InvalidResponse(
            val httpCode: Int,
            val httpStatusMessage: String?,
            val body: Response<*>?,
        ) : Error()

        /**
         * Wraps any exception thrown while executing and processing an api call.
         */
        data class Unknown(val exception: Exception) : Error()
    }

    /**
     * Returns the [Success] value if available, otherwise null.
     */
    fun getOrNull(): T? = if (this is Success) value else null

    /**
     * Returns the [Success] value if available, otherwise throws an exception.
     */
    fun get(): T = if (this is Success) value else error("Can't get success value in $this")

    /**
     * Applies [ifSuccess] if this is a [Success] and [ifError] if this is an [Error].
     *
     * @param ifSuccess the function to apply if this is a [Success].
     * @param ifError the function to apply if this is an [Error].
     *
     * @return the result of applying the correspondent function.
     */
    inline fun <R> fold(ifSuccess: (T) -> R, ifError: (Error) -> R): R = when (this) {
        is Success -> ifSuccess(value)
        is Error -> ifError(this)
    }

    /**
     * Runs the [successSideEffect] lambda function if the [TelegramBotResult] contains a successful data payload.
     *
     * @param successSideEffect A lambda that receives the successful data payload.
     *
     * @return The original instance of the [TelegramBotResult].
     */
    public inline fun onSuccess(
        crossinline successSideEffect: (T) -> Unit,
    ): TelegramBotResult<T> =
        also {
            when (it) {
                is Success -> successSideEffect(it.value)
                is Error -> Unit
            }
        }

    /**
     * Runs the [errorSideEffect] lambda function if the [TelegramBotResult] contains an error payload.
     *
     * @param errorSideEffect A lambda that receives the [Error] payload.
     *
     * @return The original instance of the [TelegramBotResult].
     */
    public inline fun onError(
        crossinline errorSideEffect: (Error) -> Unit,
    ): TelegramBotResult<T> =
        also {
            when (it) {
                is Success -> Unit
                is Error -> errorSideEffect(it)
            }
        }
}

/**
 * Returns the [Success] value if available, otherwise [default].
 */
infix fun <A : Any> TelegramBotResult<A>.getOrDefault(default: A): A = if (this is Success) value else default
