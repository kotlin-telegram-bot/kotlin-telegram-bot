package com.github.kotlintelegrambot.dispatcher.handlers.media

import anyMessage
import anySticker
import anyUpdate
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.HandleSticker
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class StickerHandlerTest {

    private val handleStickerMock = mockk<HandleSticker>(relaxed = true)

    private val sut = StickerHandler(handleStickerMock)

    @Test
    fun `checkUpdate returns false when there is no sticker`() {
        val anyUpdateWithNoSticker = anyUpdate(message = anyMessage(sticker = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoSticker)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is sticker`() {
        val anyUpdateWithSticker = anyUpdate(message = anyMessage(sticker = anySticker()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithSticker)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `sticker is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anySticker = anySticker()
        val anyMessageWithSticker = anyMessage(sticker = anySticker)
        val anyUpdateWithSticker = anyUpdate(message = anyMessageWithSticker)

        sut.handleUpdate(botMock, anyUpdateWithSticker)

        val expectedStickerHandlerEnv = MediaHandlerEnvironment(
            botMock,
            anyUpdateWithSticker,
            anyMessageWithSticker,
            anySticker
        )
        coVerify { handleStickerMock.invoke(expectedStickerHandlerEnv) }
    }
}
