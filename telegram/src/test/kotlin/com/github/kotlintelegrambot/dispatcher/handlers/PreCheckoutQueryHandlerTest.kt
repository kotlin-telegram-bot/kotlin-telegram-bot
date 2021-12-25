package com.github.kotlintelegrambot.dispatcher.handlers

import anyPreCheckoutQuery
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PreCheckoutQueryHandlerTest {

    private val handlePreCheckoutQueryMock = mockk<HandlePreCheckoutQuery>(relaxed = true)

    private val sut = PreCheckoutQueryHandler(handlePreCheckoutQueryMock)

    @Test
    fun `checkUpdate returns false when there is no pre checkout query`() {
        val anyUpdateWithNoPreCheckoutQuery = anyUpdate(preCheckoutQuery = null)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoPreCheckoutQuery)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is pre checkout query`() {
        val anyUpdateWithPreCheckoutQuery = anyUpdate(preCheckoutQuery = anyPreCheckoutQuery())

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithPreCheckoutQuery)

        Assertions.assertTrue(checkUpdateResult)
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
