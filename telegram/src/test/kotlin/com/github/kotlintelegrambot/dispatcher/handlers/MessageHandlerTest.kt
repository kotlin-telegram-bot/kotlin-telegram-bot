package com.github.kotlintelegrambot.dispatcher.handlers

import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.extensions.filters.Filter
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MessageHandlerTest {

    private val handlerMock = mockk<HandleMessage>(relaxed = true)
    private val filterMock = mockk<Filter>()

    private val sut = MessageHandler(filterMock, handlerMock)

    @Test
    fun `checkUpdate returns false when the update doesn't contain a message`() {
        val checkUpdateResult = sut.checkUpdate(anyUpdate(message = null))

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when the update contains a message and filter returns true`() {
        givenFilterReturns(true)

        val checkUpdateResult = sut.checkUpdate(anyUpdate(message = anyMessage()))

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when the update contains a message and filter returns false`() {
        givenFilterReturns(false)

        val checkUpdateResult = sut.checkUpdate(anyUpdate(message = anyMessage()))

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `message update is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyMessage = anyMessage()
        val anyUpdate = anyUpdate(message = anyMessage)

        sut.handleUpdate(botMock, anyUpdate)

        val expectedMessageHandlerEnvironment = MessageHandlerEnvironment(
            botMock,
            anyUpdate,
            anyMessage
        )
        coVerify { handlerMock.invoke(expectedMessageHandlerEnvironment) }
    }

    private fun givenFilterReturns(filterReturnValue: Boolean) {
        every { filterMock.checkFor(any()) } returns filterReturnValue
    }
}
