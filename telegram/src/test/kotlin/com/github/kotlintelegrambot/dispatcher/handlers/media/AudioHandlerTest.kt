package com.github.kotlintelegrambot.dispatcher.handlers.media

import anyAudio
import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.HandleAudio
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AudioHandlerTest {

    private val handleAudioMock = mockk<HandleAudio>(relaxed = true)

    private val sut = AudioHandler(handleAudioMock)

    @Test
    fun `checkUpdate returns false when there is no audio`() {
        val anyUpdateWithNoAudio = anyUpdate(message = anyMessage(audio = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoAudio)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is audio`() {
        val anyUpdateWithAudio = anyUpdate(message = anyMessage(audio = anyAudio()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithAudio)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `audio is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyAudio = anyAudio()
        val anyMessageWithAudio = anyMessage(audio = anyAudio)
        val anyUpdateWithAudio = anyUpdate(message = anyMessageWithAudio)

        sut.handleUpdate(botMock, anyUpdateWithAudio)

        val expectedAudioHandlerEnv = MediaHandlerEnvironment(
            botMock,
            anyUpdateWithAudio,
            anyMessageWithAudio,
            anyAudio
        )
        coVerify { handleAudioMock.invoke(expectedAudioHandlerEnv) }
    }
}
