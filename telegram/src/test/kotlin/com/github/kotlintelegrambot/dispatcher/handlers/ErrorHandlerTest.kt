package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.errors.TelegramError
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ErrorHandlerTest {

    private val handleErrorMock = mockk<HandleError>(relaxed = true)

    private val sut = ErrorHandler(handleErrorMock)

    @Test
    fun `error is properly dispatched to handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyTelegramError = mockk<TelegramError>()

        sut.invoke(botMock, anyTelegramError)

        val expectedErrorHandlerEnv = ErrorHandlerEnvironment(botMock, anyTelegramError)
        coVerify { handleErrorMock.invoke(expectedErrorHandlerEnv) }
    }
}
