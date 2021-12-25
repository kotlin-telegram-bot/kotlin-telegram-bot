package com.github.kotlintelegrambot.dispatcher.handlers.media

import anyMessage
import anyUpdate
import anyVideoNote
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.HandleVideoNote
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VideoNoteHandlerTest {

    private val handleVideoNoteMock = mockk<HandleVideoNote>(relaxed = true)

    private val sut = VideoNoteHandler(handleVideoNoteMock)

    @Test
    fun `checkUpdate returns false when there is no video note`() {
        val anyUpdateWithNoVideoNote = anyUpdate(message = anyMessage(videoNote = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoVideoNote)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is video note`() {
        val anyUpdateWithVideoNote = anyUpdate(message = anyMessage(videoNote = anyVideoNote()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithVideoNote)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `video note is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyVideoNote = anyVideoNote()
        val anyMessageWithVideoNote = anyMessage(videoNote = anyVideoNote)
        val anyUpdateWithVideoNote = anyUpdate(message = anyMessageWithVideoNote)

        sut.handleUpdate(botMock, anyUpdateWithVideoNote)

        val expectedVideoNoteHandlerEnv = MediaHandlerEnvironment(
            botMock,
            anyUpdateWithVideoNote,
            anyMessageWithVideoNote,
            anyVideoNote
        )
        coVerify { handleVideoNoteMock.invoke(expectedVideoNoteHandlerEnv) }
    }
}
