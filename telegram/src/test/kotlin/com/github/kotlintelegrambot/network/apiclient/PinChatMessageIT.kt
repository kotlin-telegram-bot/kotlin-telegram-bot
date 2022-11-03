package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.apiMethodName
import com.github.kotlintelegrambot.testutils.decode
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PinChatMessageIT : ApiClientIT() {

    @Test
    fun `pin chat message request with mandatory parameters`() {
        givenASuccessfulResponse()

        sut.pinChatMessage(
            chatId = ChatId.fromId(1L),
            messageId = 2L,
            disableNotification = null,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=1&message_id=2"
        assertEquals("pinChatMessage", request.apiMethodName)
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `pin chat message request with optional parameters`() {
        givenASuccessfulResponse()

        sut.pinChatMessage(
            chatId = ChatId.fromId(1L),
            messageId = 2L,
            disableNotification = true,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=1&message_id=2&disable_notification=true"
        assertEquals("pinChatMessage", request.apiMethodName)
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `pin chat message response`() {
        givenASuccessfulResponse()

        val pinChatMessageResult = sut.pinChatMessage(
            chatId = ChatId.fromId(1L),
            messageId = 2L,
            disableNotification = true,
        )

        assertTrue(pinChatMessageResult.get())
    }

    private fun givenASuccessfulResponse() {
        val response = """
            {
                "ok": true,
                "result": true
            }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(response)
        mockWebServer.enqueue(mockedResponse)
    }
}
