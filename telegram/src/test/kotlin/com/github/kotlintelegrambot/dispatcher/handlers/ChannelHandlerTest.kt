package com.github.kotlintelegrambot.dispatcher.handlers

import anyMessage
import anyUpdate
import com.github.kotlintelegrambot.Bot
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChannelHandlerTest {

    private val handleChannelPostMock = mockk<HandleChannelPost>(relaxed = true)

    private val sut = ChannelHandler(handleChannelPostMock)

    @Test
    fun `checkUpdate returns false when there is no channel post nor edited channel post`() {
        val anyUpdateWithNoChannelPost = anyUpdate(channelPost = null, editedChannelPost = null)

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithNoChannelPost)

        Assertions.assertFalse(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is channel post`() {
        val anyUpdateWithChannelPost = anyUpdate(channelPost = anyMessage())

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithChannelPost)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `checkUpdate returns true when there is edited channel post`() {
        val anyUpdateWithEditedChannelPost = anyUpdate(editedChannelPost = anyMessage())

        val checkUpdateResult = sut.checkUpdate(anyUpdateWithEditedChannelPost)

        Assertions.assertTrue(checkUpdateResult)
    }

    @Test
    fun `channel post is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyChannelPost = anyMessage()
        val anyUpdateWithChannelPost = anyUpdate(channelPost = anyChannelPost)

        sut.handleUpdate(botMock, anyUpdateWithChannelPost)

        val expectedChannelHandlerEnv = ChannelHandlerEnvironment(
            botMock,
            anyUpdateWithChannelPost,
            anyChannelPost,
            isEdition = false
        )
        coVerify { handleChannelPostMock.invoke(expectedChannelHandlerEnv) }
    }

    @Test
    fun `edited channel post is properly dispatched to the handler function`() = runTest {
        val botMock = mockk<Bot>()
        val anyEditedChannelPost = anyMessage()
        val anyUpdateWithEditedChannelPost = anyUpdate(editedChannelPost = anyEditedChannelPost)

        sut.handleUpdate(botMock, anyUpdateWithEditedChannelPost)

        val expectedChannelHandlerEnv = ChannelHandlerEnvironment(
            botMock,
            anyUpdateWithEditedChannelPost,
            anyEditedChannelPost,
            isEdition = true
        )
        coVerify { handleChannelPostMock.invoke(expectedChannelHandlerEnv) }
    }
}
