package com.github.kotlintelegrambot.dispatcher.handlers.media

import anyMessage
import anyUpdate
import anyVideo
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.HandleVideo
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VideoHandlerTest {

    private val handleVideoMock = mockk<HandleVideo>(relaxed = true)

    private val sut = VideoHandler(handleVideoMock)

    @Test
    fun `checkUpdate returns false when there is no video`() {
        val anyUpdateWithNoVideo = anyUpdate(message = anyMessage(video = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoVideo)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is video`() {
        val anyUpdateWithVideo = anyUpdate(message = anyMessage(video = anyVideo()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithVideo)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `video is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyVideo = anyVideo()
        val anyMessageWithVideo = anyMessage(video = anyVideo)
        val anyUpdateWithVideo = anyUpdate(message = anyMessageWithVideo)

        sut.handleUpdate(botMock, anyUpdateWithVideo)

        val expectedVideoHandlerEnv = MediaHandlerEnvironment(
            botMock,
            anyUpdateWithVideo,
            anyMessageWithVideo,
            anyVideo
        )
        coVerify { handleVideoMock.invoke(expectedVideoHandlerEnv) }
    }
}
