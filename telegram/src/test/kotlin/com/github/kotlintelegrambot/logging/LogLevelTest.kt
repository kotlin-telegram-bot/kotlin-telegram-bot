package com.github.kotlintelegrambot.logging

import com.github.kotlintelegrambot.logging.LogLevel.All
import com.github.kotlintelegrambot.logging.LogLevel.Error
import com.github.kotlintelegrambot.logging.LogLevel.Network.Basic
import com.github.kotlintelegrambot.logging.LogLevel.Network.Body
import com.github.kotlintelegrambot.logging.LogLevel.Network.Headers
import com.github.kotlintelegrambot.logging.LogLevel.Network.None
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LogLevelTest {

    @Test
    fun `log level is properly mapped to ok http's log level`() {
        assertEquals(NONE, LogLevel.None.toOkHttpLogLevel())
        assertEquals(NONE, Error.toOkHttpLogLevel())
        assertEquals(NONE, All(None).toOkHttpLogLevel())
        assertEquals(NONE, None.toOkHttpLogLevel())
        assertEquals(BASIC, All(Basic).toOkHttpLogLevel())
        assertEquals(BASIC, Basic.toOkHttpLogLevel())
        assertEquals(HEADERS, All(Headers).toOkHttpLogLevel())
        assertEquals(HEADERS, Headers.toOkHttpLogLevel())
        assertEquals(BODY, All(Body).toOkHttpLogLevel())
        assertEquals(BODY, Body.toOkHttpLogLevel())
    }

    @Test
    fun `should only log errors with All and Error`() {
        assertEquals(false, LogLevel.None.shouldLogErrors())
        assertEquals(true, Error.shouldLogErrors())
        assertEquals(true, All(None).shouldLogErrors())
        assertEquals(false, None.shouldLogErrors())
        assertEquals(true, All(Basic).shouldLogErrors())
        assertEquals(false, Basic.shouldLogErrors())
        assertEquals(true, All(Headers).shouldLogErrors())
        assertEquals(false, Headers.shouldLogErrors())
        assertEquals(true, All(Body).shouldLogErrors())
        assertEquals(false, Body.shouldLogErrors())
    }
}
