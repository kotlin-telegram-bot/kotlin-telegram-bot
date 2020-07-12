package com.github.kotlintelegrambot.logging

import junit.framework.TestCase.assertEquals
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.jupiter.api.Test

class LogLevelTest {

    @Test
    fun `log level is properly mapped to ok http's log level`() {
        assertEquals(HttpLoggingInterceptor.Level.NONE, LogLevel.None.toOkHttpLogLevel())
        assertEquals(HttpLoggingInterceptor.Level.NONE, LogLevel.Error.toOkHttpLogLevel())
        assertEquals(HttpLoggingInterceptor.Level.NONE, LogLevel.All(LogLevel.Network.None).toOkHttpLogLevel())
        assertEquals(HttpLoggingInterceptor.Level.NONE, LogLevel.Network.None.toOkHttpLogLevel())
        assertEquals(HttpLoggingInterceptor.Level.BASIC, LogLevel.All(LogLevel.Network.Basic).toOkHttpLogLevel())
        assertEquals(HttpLoggingInterceptor.Level.BASIC, LogLevel.Network.Basic.toOkHttpLogLevel())
        assertEquals(HttpLoggingInterceptor.Level.HEADERS, LogLevel.All(LogLevel.Network.Headers).toOkHttpLogLevel())
        assertEquals(HttpLoggingInterceptor.Level.HEADERS, LogLevel.Network.Headers.toOkHttpLogLevel())
        assertEquals(HttpLoggingInterceptor.Level.BODY, LogLevel.All(LogLevel.Network.Body).toOkHttpLogLevel())
        assertEquals(HttpLoggingInterceptor.Level.BODY, LogLevel.Network.Body.toOkHttpLogLevel())
    }

    @Test
    fun `should only log errors with All and Error`() {
        assertEquals(false, LogLevel.None.shouldLogErrors())
        assertEquals(true, LogLevel.Error.shouldLogErrors())
        assertEquals(true, LogLevel.All(LogLevel.Network.None).shouldLogErrors())
        assertEquals(false, LogLevel.Network.None.shouldLogErrors())
        assertEquals(true, LogLevel.All(LogLevel.Network.Basic).shouldLogErrors())
        assertEquals(false, LogLevel.Network.Basic.shouldLogErrors())
        assertEquals(true, LogLevel.All(LogLevel.Network.Headers).shouldLogErrors())
        assertEquals(false, LogLevel.Network.Headers.shouldLogErrors())
        assertEquals(true, LogLevel.All(LogLevel.Network.Body).shouldLogErrors())
        assertEquals(false, LogLevel.Network.Body.shouldLogErrors())
    }
}
