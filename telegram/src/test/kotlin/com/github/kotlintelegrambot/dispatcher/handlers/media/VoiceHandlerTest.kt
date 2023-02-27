package com.github.kotlintelegrambot.dispatcher.handlers.media

import anyMessage
import anyUpdate
import anyVoice
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.HandleVoice
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class VoiceHandlerTest {

    private val handleVoiceMock = mockk<HandleVoice>(relaxed = true)

    private val sut = VoiceHandler(handleVoiceMock)

    @Test
    fun `checkUpdate returns false when there is no voice`() {
        val anyUpdateWithNoVoice = anyUpdate(message = anyMessage(voice = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoVoice)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is voice`() {
        val anyUpdateWithVoice = anyUpdate(message = anyMessage(voice = anyVoice()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithVoice)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `voice is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyVoice = anyVoice()
        val anyMessageWithVoice = anyMessage(voice = anyVoice)
        val anyUpdateWithVoice = anyUpdate(message = anyMessageWithVoice)

        sut.handleUpdate(botMock, anyUpdateWithVoice)

        val expectedVoiceHandlerEnv = MediaHandlerEnvironment(
            botMock,
            anyUpdateWithVoice,
            anyMessageWithVoice,
            anyVoice
        )
        coVerify { handleVoiceMock.invoke(expectedVoiceHandlerEnv) }
    }
}
