package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class SetChatAdministratorCustomTitleTest : ApiClientIT() {

    @Test
    fun `setChatAdministratorCustomTitle (with chat id) arguments are properly transformed`() {
        givenAnySetChatAdministratorCustomTitleResponse()

        sut.setChatAdministratorCustomTitle(
            chatId = ANY_CHAT_ID,
            userId = ANY_USER_ID,
            customTitle = ANY_CUSTOM_TITLE
        ).execute()

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&" +
                "user_id=$ANY_USER_ID&" +
                "custom_title=$ANY_CUSTOM_TITLE"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `setChatAdministratorCustomTitle (with channel username) arguments are properly transformed`() {
        givenAnySetChatAdministratorCustomTitleResponse()

        sut.setChatAdministratorCustomTitle(
            channelUsername = ANY_CHANNEL_USERNAME,
            userId = ANY_USER_ID,
            customTitle = ANY_CUSTOM_TITLE
        ).execute()

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME&" +
                "user_id=$ANY_USER_ID&" +
                "custom_title=$ANY_CUSTOM_TITLE"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `setChatAdministratorCustomTitle success`() {
        givenSetChatAdministratorCustomTitleSuccess()

        val setChatAdministratorCustomTitleResponse = sut.setChatAdministratorCustomTitle(
            channelUsername = ANY_CHANNEL_USERNAME,
            userId = ANY_USER_ID,
            customTitle = ANY_CUSTOM_TITLE
        ).execute()

        val actualResult = setChatAdministratorCustomTitleResponse.body()?.result
        assertNotNull(actualResult)
        assertTrue(actualResult!!)
    }

    @Test
    fun `setChatAdministratorCustomTitle error`() {
        givenSetChatAdministratorCustomTitleError()

        val setChatAdministratorCustomTitleResponse = sut.setChatAdministratorCustomTitle(
            channelUsername = ANY_CHANNEL_USERNAME,
            userId = ANY_USER_ID,
            customTitle = ANY_CUSTOM_TITLE
        ).execute()

        val actualResult = setChatAdministratorCustomTitleResponse.body()?.ok
        assertNotNull(actualResult)
        assertFalse(actualResult!!)
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
