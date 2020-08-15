package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.errors.TelegramError
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class ErrorHandlerTest {

    private val handleErrorMock = mockk<HandleError>(relaxed = true)

    private val sut = ErrorHandler(handleErrorMock)

    @Test
    fun `error is properly dispatched to handler function`() {
        val botMock = mockk<Bot>()
        val anyTelegramError = mockk<TelegramError>()

        sut.invoke(botMock, anyTelegramError)

        val expectedErrorHandlerEnv = ErrorHandlerEnvironment(botMock, anyTelegramError)
        verify { handleErrorMock.invoke(expectedErrorHandlerEnv) }
    }
}
