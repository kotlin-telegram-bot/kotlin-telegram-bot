package com.github.kotlintelegrambot.dispatcher.handlers

import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class TextEditHandlerTest {

    private val handleTextMock = mockk<HandleText>(relaxed = true)

    @Test
    fun `checkUpdate returns false when update has no editedMessage`() {
        val anyUpdateWithNoMessage = anyUpdate(editedMessage = null)
        val sut = TextEditHandler(text = "", handleText = handleTextMock)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoMessage)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when editedMessage has no text`() {
        val anyMessageWithNoText = anyUpdate(editedMessage = anyMessage(text = null))
        val sut = TextEditHandler(text = "", handleText = handleTextMock)

        val checkUpdateResult = sut.checkUpdate(anyMessageWithNoText)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when editedMessage has text and there is no text to match`() {
        val anyMessageWithText = anyUpdate(editedMessage = anyMessage(text = ANY_TEXT))
        val sut = TextEditHandler(text = null, handleText = handleTextMock)

        val checkUpdateResult = sut.checkUpdate(anyMessageWithText)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when editedMessage has text and it doesn't match the text to match`() {
        val anyMessageWithText = anyUpdate(editedMessage = anyMessage(text = ANY_TEXT))
        val sut = TextEditHandler(text = ANY_OTHER_TEXT, handleText = handleTextMock)

        val checkUpdateResult = sut.checkUpdate(anyMessageWithText)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when editedMessage has text and it's equal to the text to match`() {
        val anyMessageWithText = anyUpdate(editedMessage = anyMessage(text = ANY_TEXT))
        val sut = TextEditHandler(text = ANY_TEXT, handleText = handleTextMock)

        val checkUpdateResult = sut.checkUpdate(anyMessageWithText)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when editedMessage has text and it contains the text to match`() {
        val anyMessageWithText = anyUpdate(editedMessage = anyMessage(text = ANY_TEXT))
        val sut = TextEditHandler(text = ANY_TEXT_CONTAINED_IN_ANY_TEXT, handleText = handleTextMock)

        val checkUpdateResult = sut.checkUpdate(anyMessageWithText)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `text is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyMessageWithText = anyMessage(text = ANY_TEXT)
        val anyUpdate = anyUpdate(editedMessage = anyMessageWithText)
        val sut = TextEditHandler(text = ANY_TEXT, handleText = handleTextMock)

        sut.handleUpdate(botMock, anyUpdate)

        val expectedTextHandlerEnvironment = TextHandlerEnvironment(
            botMock,
            anyUpdate,
            anyMessageWithText,
            ANY_TEXT,
        )
        coVerify { handleTextMock.invoke(expectedTextHandlerEnvironment) }
    }

    private companion object {
        const val ANY_TEXT = "Valar Morghulis"
        const val ANY_OTHER_TEXT = "Valar Dohaeris"
        const val ANY_TEXT_CONTAINED_IN_ANY_TEXT = "Valar"
    }
}
