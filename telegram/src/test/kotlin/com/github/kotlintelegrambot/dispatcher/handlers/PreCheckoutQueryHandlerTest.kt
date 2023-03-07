package com.github.kotlintelegrambot.dispatcher.handlers

import anyPreCheckoutQuery
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class PreCheckoutQueryHandlerTest {

    private val handlePreCheckoutQueryMock = mockk<HandlePreCheckoutQuery>(relaxed = true)

    private val sut = PreCheckoutQueryHandler(handlePreCheckoutQueryMock)

    @Test
    fun `checkUpdate returns false when there is no pre checkout query`() {
        val anyUpdateWithNoPreCheckoutQuery = anyUpdate(preCheckoutQuery = null)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoPreCheckoutQuery)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is pre checkout query`() {
        val anyUpdateWithPreCheckoutQuery = anyUpdate(preCheckoutQuery = anyPreCheckoutQuery())

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithPreCheckoutQuery)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `pre checkout query is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyPreCheckoutQuery = anyPreCheckoutQuery()
        val anyUpdateWithPreCheckoutQuery = anyUpdate(preCheckoutQuery = anyPreCheckoutQuery)

        sut.handleUpdate(botMock, anyUpdateWithPreCheckoutQuery)

        val expectedPreCheckoutQueryHandlerEnv = PreCheckoutQueryHandlerEnvironment(
            botMock,
            anyUpdateWithPreCheckoutQuery,
            anyPreCheckoutQuery
        )
        coVerify { handlePreCheckoutQueryMock.invoke(expectedPreCheckoutQueryHandlerEnv) }
    }
}
