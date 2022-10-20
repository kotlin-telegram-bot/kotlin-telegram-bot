package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.apiMethodName
import com.github.kotlintelegrambot.testutils.decode
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LeaveChatIT : ApiClientIT() {

    @Test
    fun `leave chat request`() {
        givenASuccessfulResponse()

        sut.leaveChat(ChatId.fromId(41342L))

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=41342"
        assertEquals("leaveChat", request.apiMethodName)
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `leave chat response`() {
        givenASuccessfulResponse()

        val leaveChatResponse = sut.leaveChat(ChatId.fromId(41342L))

        assertTrue(leaveChatResponse.get())
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
