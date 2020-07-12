package com.github.kotlintelegrambot.logging

import com.github.kotlintelegrambot.logging.LogLevel.All
import com.github.kotlintelegrambot.logging.LogLevel.Error
import com.github.kotlintelegrambot.logging.LogLevel.Network
import com.github.kotlintelegrambot.logging.LogLevel.Network.Basic
import com.github.kotlintelegrambot.logging.LogLevel.Network.Body
import com.github.kotlintelegrambot.logging.LogLevel.Network.Headers
import com.github.kotlintelegrambot.logging.LogLevel.None
import okhttp3.logging.HttpLoggingInterceptor

sealed class LogLevel {
    /** No logs **/
    object None : LogLevel()

    /** Logs network requests, network responses and uncaught exceptions
     * thrown in handlers execution **/
    data class All(
        val networkLogLevel: LogLevel.Network = Body
    ) : LogLevel()

    /** Logs network requests and responses information **/
    sealed class Network : LogLevel() {
        /** No logs **/
        object None : Network()
        /**
         * Logs requests and responses lines.
         *
         * Example:
         * --> POST /test http/1.1 (8-byte body)
         * <-- 200 OK (29ms, 4-byte body)
         */
        object Basic : Network()
        /**
         * Logs requests and responses lines and their respective headers.
         *
         * Example:
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         */
        object Headers : Network()
        /**
         * Logs requests and responses lines and their respective headers and bodies (if present).
         *
         * Example:
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         */
        object Body : Network()
    }

    /** Logs uncaught exceptions thrown in the handlers execution **/
    object Error : LogLevel()

    internal fun shouldLogErrors(): Boolean = this is All || this is Error
}

internal fun LogLevel.toOkHttpLogLevel(): HttpLoggingInterceptor.Level = when (this) {
    None -> HttpLoggingInterceptor.Level.NONE
    is All -> networkLogLevel.toOkHttpLogLevel()
    is Network -> toOkHttpLogLevel()
    Error -> HttpLoggingInterceptor.Level.NONE
}

private fun Network.toOkHttpLogLevel(): HttpLoggingInterceptor.Level = when (this) {
    Network.None -> HttpLoggingInterceptor.Level.NONE
    Basic -> HttpLoggingInterceptor.Level.BASIC
    Headers -> HttpLoggingInterceptor.Level.HEADERS
    Body -> HttpLoggingInterceptor.Level.BODY
}
