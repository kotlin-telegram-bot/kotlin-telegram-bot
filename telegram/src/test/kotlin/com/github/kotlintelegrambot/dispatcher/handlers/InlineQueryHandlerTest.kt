package com.github.kotlintelegrambot.dispatcher.handlers

import anyInlineQuery
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class InlineQueryHandlerTest {

    private val handleInlineQueryMock = mockk<HandleInlineQuery>(relaxed = true)

    private val sut = InlineQueryHandler(handleInlineQueryMock)

    @Test
    fun `checkUpdate returns true when there is inline query`() {
        val anyUpdateWithInlineQuery = anyUpdate(inlineQuery = anyInlineQuery())

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithInlineQuery)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when there is no inline query`() {
        val anyUpdateWithoutInlineQuery = anyUpdate(inlineQuery = null)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithoutInlineQuery)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `inline query is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyInlineQuery = anyInlineQuery()
        val anyUpdateWithInlineQuery = anyUpdate(inlineQuery = anyInlineQuery)

        sut.handleUpdate(botMock, anyUpdateWithInlineQuery)

        val expectedInlineQueryHandlerEnv = InlineQueryHandlerEnvironment(
            botMock,
            anyUpdateWithInlineQuery,
            anyInlineQuery
        )
        coVerify { handleInlineQueryMock.invoke(expectedInlineQueryHandlerEnv) }
    }
}
