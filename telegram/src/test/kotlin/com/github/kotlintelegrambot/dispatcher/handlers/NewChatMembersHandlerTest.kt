package com.github.kotlintelegrambot.dispatcher.handlers

import anyMessage
import anyUpdate
import anyUser
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class NewChatMembersHandlerTest {

    private val handleNewChatMembersHandlerMock = mockk<HandleNewChatMembers>(relaxed = true)

    private val sut = NewChatMembersHandler(handleNewChatMembersHandlerMock)

    @Test
    fun `checkUpdate returns false when there is no message`() {
        val anyUpdateWithNoMessage = anyUpdate(message = null)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoMessage)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when there are no new chat members`() {
        val anyUpdateWithNoChatMembers = anyUpdate(message = anyMessage(newChatMembers = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoChatMembers)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when new chat members are empty`() {
        val anyUpdateWithEmptyNewChatMembers = anyUpdate(message = anyMessage(newChatMembers = emptyList()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithEmptyNewChatMembers)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when new chat members are not empty`() {
        val anyUpdateWithNewChatMembers = anyUpdate(message = anyMessage(newChatMembers = listOf(anyUser())))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNewChatMembers)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `new chat members are properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyNewChatMembers = listOf(anyUser())
        val anyMessageWithNewChatMembers = anyMessage(newChatMembers = anyNewChatMembers)
        val anyUpdateWithNewChatMembers = anyUpdate(message = anyMessageWithNewChatMembers)

        sut.handleUpdate(botMock, anyUpdateWithNewChatMembers)

        val expectedNewChatMembersHandlerEnv = NewChatMembersHandlerEnvironment(
            botMock,
            anyUpdateWithNewChatMembers,
            anyMessageWithNewChatMembers,
            anyNewChatMembers
        )
        coVerify { handleNewChatMembersHandlerMock.invoke(expectedNewChatMembersHandlerEnv) }
    }
}
