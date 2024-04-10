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

class MyChatMemberHandlerTest {

    private val handleMyChatMemberMock = mockk<HandleMyChatMember>(relaxed = true)

    @Test
    fun `Should returns false when there is no my chat member`() {
        val anyUpdateWithoutMyChatMember = anyUpdate(myChatMember = null)
        val sut = MyChatMemberHandler(handleMyChatMember = handleMyChatMemberMock)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithoutMyChatMember)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `Should returns true when there is my chat member`() {
        val anyUpdateWithMyChatMember = anyUpdate(myChatMember = anyMyChatMember())
        val sut = MyChatMemberHandler(handleMyChatMember = handleMyChatMemberMock)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithMyChatMember)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `myChatMember is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>(relaxed = true)
        val anyMyChatMember = anyMyChatMember(chat = anyChat())
        val anyUpdateWithMyChatMember = anyUpdate(myChatMember = anyMyChatMember)

        val sut = MyChatMemberHandler(handleMyChatMember = handleMyChatMemberMock)

        sut.handleUpdate(botMock, anyUpdateWithMyChatMember)

        val expectedMyChatMemberHandlerEnvironment = MyChatMemberHandlerEnvironment(
            botMock,
            anyUpdateWithMyChatMember,
            anyMyChatMember,
        )
        coVerify { handleMyChatMemberMock.invoke(expectedMyChatMemberHandlerEnvironment) }
    }
}
