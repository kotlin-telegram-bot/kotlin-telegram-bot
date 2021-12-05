package com.github.kotlintelegrambot.dispatcher.handlers

import anyChatJoinRequest
import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase
import org.junit.jupiter.api.Test

class ChatJoinRequestHandlerTest {
    private val handlerFunctionMock = mockk<ChatJoinRequestHandlerEnvironment.() -> Unit>(relaxed = true)

    private val sut = ChatJoinRequestHandler(handlerFunctionMock)

    @Test
    fun `checkUpdate returns false for the update with non null message`() {
        val anyCommand = anyUpdate(message = anyMessage())

        val checkUpdateResult = sut.checkUpdate(anyCommand)

        TestCase.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true for the update with chatJoinRequest and null message`() {
        val anyCommandWithArguments = anyUpdate(
            message = null,
            chatJoinRequest = anyChatJoinRequest()
        )

        val checkUpdateResult = sut.checkUpdate(anyCommandWithArguments)

        TestCase.assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false for the update with empty message and chatJoinRequest`() {
        val anyOtherCommand = anyUpdate(message = null, chatJoinRequest = null)

        val checkUpdateResult = sut.checkUpdate(anyOtherCommand)

        TestCase.assertFalse(checkUpdateResult)
    }

    @Test
    fun `chatJoinRequest update is properly dispatched to the handler function`() {
        val botMock = mockk<Bot>()
        val request = anyChatJoinRequest()
        val anyUpdate = anyUpdate(message = null, chatJoinRequest = anyChatJoinRequest())

        sut.handleUpdate(botMock, anyUpdate)

        val expectedArgs = ChatJoinRequestHandlerEnvironment(botMock, anyUpdate, request)
        verify { handlerFunctionMock.invoke(expectedArgs) }
    }
}
