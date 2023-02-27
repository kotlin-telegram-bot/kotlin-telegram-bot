package com.github.kotlintelegrambot.dispatcher.handlers.media

import anyGame
import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.HandleGame
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class GameHandlerTest {

    private val handleGameMock = mockk<HandleGame>(relaxed = true)

    private val sut = GameHandler(handleGameMock)

    @Test
    fun `checkUpdate returns false when there is no game`() {
        val anyUpdateWithNoGame = anyUpdate(message = anyMessage(game = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoGame)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is game`() {
        val anyUpdateWithGame = anyUpdate(message = anyMessage(game = anyGame()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithGame)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `game is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyGame = anyGame()
        val anyMessageWithGame = anyMessage(game = anyGame)
        val anyUpdateWithGame = anyUpdate(message = anyMessageWithGame)

        sut.handleUpdate(botMock, anyUpdateWithGame)

        val expectedGameHandlerEnv = MediaHandlerEnvironment(
            botMock,
            anyUpdateWithGame,
            anyMessageWithGame,
            anyGame
        )
        coVerify { handleGameMock.invoke(expectedGameHandlerEnv) }
    }
}
