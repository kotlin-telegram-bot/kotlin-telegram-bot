package com.github.kotlintelegrambot.dispatcher.handlers

import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.HandleUpdate
import com.github.kotlintelegrambot.extensions.filters.Filter
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MessageHandlerTest {

    private val handlerCallbackMock = mockk<HandleUpdate>()
    private val filterMock = mockk<Filter>()

    private val sut = MessageHandler(
        handlerCallback = handlerCallbackMock,
        filter = filterMock
    )

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

    private fun givenFilterReturns(filterReturnValue: Boolean) {
        every { filterMock.checkFor(any()) } returns filterReturnValue
    }
}
