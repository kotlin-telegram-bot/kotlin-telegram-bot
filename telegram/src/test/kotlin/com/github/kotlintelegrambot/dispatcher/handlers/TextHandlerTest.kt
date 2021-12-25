package com.github.kotlintelegrambot.dispatcher.handlers

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
class TextHandlerTest {

    private val handleTextMock = mockk<HandleText>(relaxed = true)

    @Test
    fun `checkUpdate returns false when update has no message`() {
        val anyUpdateWithNoMessage = anyUpdate(message = null)
        val sut = TextHandler(text = "", handleText = handleTextMock)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoMessage)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when message has no text`() {
        val anyMessageWithNoText = anyUpdate(message = anyMessage(text = null))
        val sut = TextHandler(text = "", handleText = handleTextMock)

        val checkUpdateResult = sut.checkUpdate(anyMessageWithNoText)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when message has text and there is no text to match`() {
        val anyMessageWithText = anyUpdate(message = anyMessage(text = ANY_TEXT))
        val sut = TextHandler(text = null, handleText = handleTextMock)

        val checkUpdateResult = sut.checkUpdate(anyMessageWithText)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when message has text and it doesn't match the text to match`() {
        val anyMessageWithText = anyUpdate(message = anyMessage(text = ANY_TEXT))
        val sut = TextHandler(text = ANY_OTHER_TEXT, handleText = handleTextMock)

        val checkUpdateResult = sut.checkUpdate(anyMessageWithText)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when message has text and it's equal to the text to match`() {
        val anyMessageWithText = anyUpdate(message = anyMessage(text = ANY_TEXT))
        val sut = TextHandler(text = ANY_TEXT, handleText = handleTextMock)

        val checkUpdateResult = sut.checkUpdate(anyMessageWithText)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when message has text and it contains the text to match`() {
        val anyMessageWithText = anyUpdate(message = anyMessage(text = ANY_TEXT))
        val sut = TextHandler(text = ANY_TEXT_CONTAINED_IN_ANY_TEXT, handleText = handleTextMock)

        val checkUpdateResult = sut.checkUpdate(anyMessageWithText)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `text is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyMessageWithText = anyMessage(text = ANY_TEXT)
        val anyUpdate = anyUpdate(message = anyMessageWithText)
        val sut = TextHandler(text = ANY_TEXT, handleText = handleTextMock)

        sut.handleUpdate(botMock, anyUpdate)

        val expectedTextHandlerEnvironment = TextHandlerEnvironment(
            botMock,
            anyUpdate,
            anyMessageWithText,
            ANY_TEXT
        )
        coVerify { handleTextMock.invoke(expectedTextHandlerEnvironment) }
    }

    private companion object {
        const val ANY_TEXT = "Valar Morghulis"
        const val ANY_OTHER_TEXT = "Valar Dohaeris"
        const val ANY_TEXT_CONTAINED_IN_ANY_TEXT = "Valar"
    }
}
