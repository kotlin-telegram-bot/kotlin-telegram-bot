package com.github.kotlintelegrambot.dispatcher.handlers

import anyChatMember
import anyMyChatMember
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MyChatMemberHandlerTest {

    private val handleMyChatMemberMock = mockk<HandleMyChatMember>(relaxed = true)

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
        val sut = MyChatMemberHandler(newChatMemberStatus = null, handleMyChatMember = handleMyChatMemberMock)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithMyChatMember)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns false when there is my chat member and its status doesn't match the status to match`() {
        val anyUpdateWithMyChatMember = anyUpdate(
            myChatMember = anyMyChatMember(newChatMember = anyChatMember(status = ANY_CHAT_MEMBER_STATUS))
        )

        val sut = MyChatMemberHandler(
            newChatMemberStatus = ANY_OTHER_CHAT_MEMBER_STATUS,
            handleMyChatMember = handleMyChatMemberMock
        )

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithMyChatMember)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is my chat member and its status is equal to the status to match`() {
        val anyUpdateWithMyChatMember = anyUpdate(
            myChatMember = anyMyChatMember(newChatMember = anyChatMember(status = ANY_CHAT_MEMBER_STATUS))
        )

        val sut = MyChatMemberHandler(
            newChatMemberStatus = ANY_CHAT_MEMBER_STATUS,
            handleMyChatMember = handleMyChatMemberMock
        )

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithMyChatMember)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `myChatMember is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>(relaxed = true)
        val anyMyChatMember = anyMyChatMember(newChatMember = anyChatMember(status = ANY_CHAT_MEMBER_STATUS))
        val anyUpdateWithMyChatMember = anyUpdate(myChatMember = anyMyChatMember)

        val sut = MyChatMemberHandler(
            newChatMemberStatus = ANY_CHAT_MEMBER_STATUS,
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

    private companion object {

        const val ANY_CHAT_MEMBER_STATUS = "member"
        const val ANY_OTHER_CHAT_MEMBER_STATUS = "kicked"
    }
}
