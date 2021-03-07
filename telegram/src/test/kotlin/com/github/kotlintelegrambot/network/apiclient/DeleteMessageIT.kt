package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class DeleteMessageIT : ApiClientIT() {

    @Test
    fun `deleteMessage with chat id sends correct request`() {
        givenDeleteMessageSuccessfulResponse()

        sut.deleteMessage(ChatId.fromId(ANY_CHAT_ID), ANY_MESSAGE_ID)

        val requestBody = mockWebServer.takeRequest().body.readUtf8().decode()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&message_id=$ANY_MESSAGE_ID"
        assertEquals(expectedRequestBody, requestBody)
    }

    @Test
    fun `deleteMessage with channel username sends correct request`() {
        givenDeleteMessageSuccessfulResponse()

        sut.deleteMessage(ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME), ANY_MESSAGE_ID)

        val requestBody = mockWebServer.takeRequest().body.readUtf8().decode()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME&message_id=$ANY_MESSAGE_ID"
        assertEquals(expectedRequestBody, requestBody)
    }

    @Test
    fun `deleteMessage response is returned correctly`() {
        givenDeleteMessageSuccessfulResponse()

        val deleteMessageResult = sut.deleteMessage(ChatId.fromId(ANY_CHAT_ID), ANY_MESSAGE_ID)

        assertTrue(deleteMessageResult.get())
    }

    private fun givenDeleteMessageSuccessfulResponse() {
        val deleteMessageResponse = """
            {
                "ok": true,
                "result": true
            }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(deleteMessageResponse)
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 31513251325L
        const val ANY_CHANNEL_USERNAME = "@telegramchannel"
        const val ANY_MESSAGE_ID = 423523523L
    }
}
