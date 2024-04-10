package com.github.kotlintelegrambot.dispatcher.handlers

import anyChat
import anyMyChatMember
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ChatMemberHandlerTest {

    private val handleChatMemberMock = mockk<HandleChatMember>(relaxed = true)

    @Test
    fun `Should returns false when there is no my chat member`() {
        val anyUpdateWithoutMyChatMember = anyUpdate(chatMember = null)
        val sut = ChatMemberHandler(handleChatMember = handleChatMemberMock)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithoutMyChatMember)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `Should returns true when there is my chat member`() {
        val anyUpdateWithMyChatMember = anyUpdate(chatMember = anyMyChatMember())
        val sut = ChatMemberHandler(handleChatMember = handleChatMemberMock)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithMyChatMember)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `myChatMember is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>(relaxed = true)
        val anyChatMember = anyMyChatMember(chat = anyChat())
        val anyUpdateWithMyChatMember = anyUpdate(chatMember = anyChatMember)

        val sut = ChatMemberHandler(handleChatMember = handleChatMemberMock)

        sut.handleUpdate(botMock, anyUpdateWithMyChatMember)

        val expectedChatMemberHandlerEnvironment = ChatMemberHandlerEnvironment(
            botMock,
            anyUpdateWithMyChatMember,
            anyChatMember,
        )
        coVerify { handleChatMemberMock.invoke(expectedChatMemberHandlerEnvironment) }
    }
}
