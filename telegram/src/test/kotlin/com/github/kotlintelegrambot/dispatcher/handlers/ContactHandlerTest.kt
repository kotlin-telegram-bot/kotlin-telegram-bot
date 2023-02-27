package com.github.kotlintelegrambot.dispatcher.handlers

import anyContact
import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class ContactHandlerTest {

    private val handleContactMock = mockk<HandleContact>(relaxed = true)

    private val sut = ContactHandler(handleContactMock)

    @Test
    fun `checkUpdate returns false when there is no contact`() {
        val anyUpdateWithNoContact = anyUpdate(message = anyMessage(contact = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoContact)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is contact`() {
        val anyUpdateWithContact = anyUpdate(message = anyMessage(contact = anyContact()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithContact)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `contact is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyContact = anyContact()
        val anyMessageWithContact = anyMessage(contact = anyContact)
        val anyUpdateWithContact = anyUpdate(message = anyMessageWithContact)

        sut.handleUpdate(botMock, anyUpdateWithContact)

        val expectedCommandHandlerEnv = ContactHandlerEnvironment(
            botMock,
            anyUpdateWithContact,
            anyMessageWithContact,
            anyContact
        )
        coVerify { handleContactMock.invoke(expectedCommandHandlerEnv) }
    }
}
