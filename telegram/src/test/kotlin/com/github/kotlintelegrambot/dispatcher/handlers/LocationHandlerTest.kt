package com.github.kotlintelegrambot.dispatcher.handlers

import anyLocation
import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocationHandlerTest {

    private val handleLocationMock = mockk<HandleLocation>(relaxed = true)

    private val sut = LocationHandler(handleLocationMock)

    @Test
    fun `checkUpdate returns false when there is no location`() {
        val anyUpdateWithNoLocation = anyUpdate(message = anyMessage(location = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoLocation)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is location`() {
        val anyUpdateWithLocation = anyUpdate(message = anyMessage(location = anyLocation()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithLocation)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `location is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyLocation = anyLocation()
        val anyMessageWithLocation = anyMessage(location = anyLocation)
        val anyUpdateWithLocation = anyUpdate(message = anyMessageWithLocation)

        sut.handleUpdate(botMock, anyUpdateWithLocation)

        val expectedLocationHandlerEnv = LocationHandlerEnvironment(
            botMock,
            anyUpdateWithLocation,
            anyMessageWithLocation,
            anyLocation
        )
        coVerify { handleLocationMock.invoke(expectedLocationHandlerEnv) }
    }
}
