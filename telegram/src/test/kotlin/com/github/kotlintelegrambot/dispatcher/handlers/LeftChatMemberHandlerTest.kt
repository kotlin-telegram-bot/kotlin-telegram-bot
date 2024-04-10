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

class LeftChatMemberHandlerTest {

    private val handleLeftChatMemberHandlerMock = mockk<HandleLeftChatMember>(relaxed = true)

    private val sut = LeftChatMemberHandler(handleLeftChatMemberHandlerMock)

    @Test
    fun `checkUpdate returns false when there is no message`() {
        val anyUpdateWithNoMessage = anyUpdate(message = null)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoMessage)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when there are no left chat member`() {
        val anyUpdateWithNoLeftChatMember = anyUpdate(message = anyMessage(leftChatMember = null))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoLeftChatMember)

        assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when chat member left`() {
        val anyUpdateWithLeftChatMember = anyUpdate(message = anyMessage(leftChatMember = anyUser()))

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithLeftChatMember)

        assertTrue(checkUpdateResult)
    }

    @Test
    fun `new chat members are properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyLeftChatMember = anyUser()
        val anyMessageWithNewChatMember = anyMessage(leftChatMember = anyLeftChatMember)
        val anyUpdateWithLeftChatMember = anyUpdate(message = anyMessageWithNewChatMember)

        sut.handleUpdate(botMock, anyUpdateWithLeftChatMember)

        val expectedLeftChatMemberHandlerEnv = LeftChatMemberHandlerEnvironment(
            botMock,
            anyUpdateWithLeftChatMember,
            anyMessageWithNewChatMember,
            anyLeftChatMember,
        )
        coVerify { handleLeftChatMemberHandlerMock.invoke(expectedLeftChatMemberHandlerEnv) }
    }
}
