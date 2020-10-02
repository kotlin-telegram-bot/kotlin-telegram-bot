package com.github.kotlintelegrambot.dispatcher.handlers.media

import anyDocument
import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.HandleDocument
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Test

class DocumentHandlerTest {

    private val handleDocumentMock = mockk<HandleDocument>(relaxed = true)

    private val sut = DocumentHandler(handleDocumentMock)

    @Test
    fun `checkUpdate returns false when there is no document`() {
        val anyUpdateWithNoDocument = anyUpdate(message = anyMessage(document = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoDocument)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is document`() {
        val anyUpdateWithDocument = anyUpdate(message = anyMessage(document = anyDocument()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithDocument)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `document is properly dispatched to the handler function`() {
        val botMock = mockk<Bot>()
        val anyDocument = anyDocument()
        val anyMessageWithDocument = anyMessage(document = anyDocument)
        val anyUpdateWithDocument = anyUpdate(message = anyMessageWithDocument)

        sut(botMock, anyUpdateWithDocument)

        val expectedDocumentHandlerEnv = MediaHandlerEnvironment(
            botMock,
            anyUpdateWithDocument,
            anyMessageWithDocument,
            anyDocument
        )
        verify { handleDocumentMock.invoke(expectedDocumentHandlerEnv) }
    }
}
