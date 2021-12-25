package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.decode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SetChatAdministratorCustomTitleTest : ApiClientIT() {

    @Test
    fun `setChatAdministratorCustomTitle (with chat id) arguments are properly transformed`() = runTest {
        givenAnySetChatAdministratorCustomTitleResponse()

        sut.setChatAdministratorCustomTitle(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            userId = ANY_USER_ID,
            customTitle = ANY_CUSTOM_TITLE
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&" +
            "user_id=$ANY_USER_ID&" +
            "custom_title=$ANY_CUSTOM_TITLE"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `setChatAdministratorCustomTitle (with channel username) arguments are properly transformed`() = runTest {
        givenAnySetChatAdministratorCustomTitleResponse()

        sut.setChatAdministratorCustomTitle(
            chatId = ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            userId = ANY_USER_ID,
            customTitle = ANY_CUSTOM_TITLE
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME&" +
            "user_id=$ANY_USER_ID&" +
            "custom_title=$ANY_CUSTOM_TITLE"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `setChatAdministratorCustomTitle success`() = runTest {
        givenSetChatAdministratorCustomTitleSuccess()

        val setChatAdministratorCustomTitleResponse = sut.setChatAdministratorCustomTitle(
            chatId = ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            userId = ANY_USER_ID,
            customTitle = ANY_CUSTOM_TITLE
        )

        Assertions.assertTrue(setChatAdministratorCustomTitleResponse.get())
    }

    @Test
    fun `setChatAdministratorCustomTitle error`() = runTest {
        givenSetChatAdministratorCustomTitleError()

        val setChatAdministratorCustomTitleResponse = sut.setChatAdministratorCustomTitle(
            chatId = ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            userId = ANY_USER_ID,
            customTitle = ANY_CUSTOM_TITLE
        )

        Assertions.assertNull(setChatAdministratorCustomTitleResponse.getOrNull())
    }

    private fun givenAnySetChatAdministratorCustomTitleResponse() {
        givenSetChatAdministratorCustomTitleSuccess()
    }

    private fun givenSetChatAdministratorCustomTitleSuccess() {
        val setChatAdministratorCustomTitleResponse = """
            {
                "ok": true,
                "result": true 
            }
        """.trimIndent()

        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(setChatAdministratorCustomTitleResponse)
        mockWebServer.enqueue(mockedResponse)
    }

    private fun givenSetChatAdministratorCustomTitleError() {
        val setChatAdministratorCustomTitleResponse = """
            {
                "ok": false
            }
        """.trimIndent()

        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(setChatAdministratorCustomTitleResponse)
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 135332523423L
        const val ANY_CHANNEL_USERNAME = "@winterfell"
        const val ANY_USER_ID = 32524523L
        const val ANY_CUSTOM_TITLE = "Rukon Stark"
    }
}
