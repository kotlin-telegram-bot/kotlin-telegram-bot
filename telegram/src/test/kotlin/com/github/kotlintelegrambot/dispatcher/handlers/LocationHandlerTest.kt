package com.github.kotlintelegrambot.dispatcher.handlers

import anyLocation
import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.HandleLocation
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Test

class LocationHandlerTest {

    private val handleLocationMock = mockk<HandleLocation>(relaxed = true)

    private val sut = LocationHandler(handleLocationMock)

    @Test
    fun `checkUpdate returns false when there is no location`() {
        val anyUpdateWithNoLocation = anyUpdate(message = anyMessage(location = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoLocation)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is location`() {
        val anyUpdateWithLocation = anyUpdate(message = anyMessage(location = anyLocation()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithLocation)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `location is properly dispatched to the handler function`() {
        val botMock = mockk<Bot>()
        val anyLocation = anyLocation()
        val anyMessageWithLocation = anyMessage(location = anyLocation)
        val anyUpdateWithLocation = anyUpdate(message = anyMessageWithLocation)

        sut.handlerCallback(botMock, anyUpdateWithLocation)

        val expectedLocationHandlerEnv = LocationHandlerEnvironment(
            botMock,
            anyUpdateWithLocation,
            anyMessageWithLocation,
            anyLocation
        )
        verify { handleLocationMock.invoke(expectedLocationHandlerEnv) }
    }
}
