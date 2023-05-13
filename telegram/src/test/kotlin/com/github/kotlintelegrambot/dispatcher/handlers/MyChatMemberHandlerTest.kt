package com.github.kotlintelegrambot.dispatcher.handlers

import anyChat
import anyMyChatMember
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MyChatMemberHandlerTest {

    private val handleMyChatMemberMock = mockk<HandleMyChatMember>(relaxed = true)

    private lateinit var chatType: String
    private lateinit var otherChatType: String

    private companion object {
        private val possibleChatTypes = listOf("private", "group", "supergroup", "channel")
    }

    @BeforeEach
    fun regenerateValues() {
        chatType = possibleChatTypes.random()
        otherChatType = possibleChatTypes.filterNot { it == chatType }.random()
    }

    @Test
    fun `checkUpdate returns false when there is no my chat member`() {
        val anyUpdateWithoutMyChatMember = anyUpdate(myChatMember = null)
        val sut = MyChatMemberHandler(handleMyChatMember = handleMyChatMemberMock)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithoutMyChatMember)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is my chat member and no chat status to match`() {
        val anyUpdateWithMyChatMember = anyUpdate(myChatMember = anyMyChatMember())
        val sut = MyChatMemberHandler(chatType = null, handleMyChatMember = handleMyChatMemberMock)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithMyChatMember)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when there is my chat member and its status doesn't match the status to match`() {
        val anyUpdateWithMyChatMember = anyUpdate(
            myChatMember = anyMyChatMember(chat = anyChat(type = chatType))
        )

        val sut = MyChatMemberHandler(
            chatType = otherChatType,
            handleMyChatMember = handleMyChatMemberMock
        )

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithMyChatMember)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is my chat member and its status is equal to the status to match`() {
        val anyUpdateWithMyChatMember = anyUpdate(
            myChatMember = anyMyChatMember(chat = anyChat(type = chatType))
        )

        val sut = MyChatMemberHandler(
            chatType = chatType,
            handleMyChatMember = handleMyChatMemberMock
        )

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithMyChatMember)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `myChatMember is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>(relaxed = true)
        val anyMyChatMember = anyMyChatMember(chat = anyChat(type = chatType))
        val anyUpdateWithMyChatMember = anyUpdate(myChatMember = anyMyChatMember)

        val sut = MyChatMemberHandler(
            chatType = chatType,
            handleMyChatMember = handleMyChatMemberMock
        )

        sut.handleUpdate(botMock, anyUpdateWithMyChatMember)

        val expectedMyChatMemberHandlerEnvironment = MyChatMemberHandlerEnvironment(
            botMock,
            anyUpdateWithMyChatMember,
            anyMyChatMember
        )
        coVerify { handleMyChatMemberMock.invoke(expectedMyChatMemberHandlerEnvironment) }
    }
}
