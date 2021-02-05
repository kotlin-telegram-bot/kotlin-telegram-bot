package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class UnpinChatMessageIT : ApiClientIT() {

    @Test
    fun `correct request on unpinChatMessage with chat id and message id`() {
        givenASuccessfulUnpinChatMessageResponse()

        sut.unpinChatMessage(ChatId.fromId(ANY_CHAT_ID), ANY_MESSAGE_ID)

        val request = mockWebServer.takeRequest()
        val requestMethodName = request.path?.split("/")?.lastOrNull()
        val requestBody = request.body.readUtf8().decode()
        assertEquals("unpinChatMessage", requestMethodName)
        assertEquals("chat_id=$ANY_CHAT_ID&message_id=$ANY_MESSAGE_ID", requestBody)
    }

    @Test
    fun `correct request on unpinChatMessage with chat id and no message id`() {
        givenASuccessfulUnpinChatMessageResponse()

        sut.unpinChatMessage(ChatId.fromId(ANY_CHAT_ID), messageId = null)

        val request = mockWebServer.takeRequest()
        val requestMethodName = request.path?.split("/")?.lastOrNull()
        val requestBody = request.body.readUtf8().decode()
        assertEquals("unpinChatMessage", requestMethodName)
        assertEquals("chat_id=$ANY_CHAT_ID", requestBody)
    }

    @Test
    fun `correct request on unpinChatMessage with channel username and message id`() {
        givenASuccessfulUnpinChatMessageResponse()

        sut.unpinChatMessage(ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME), ANY_MESSAGE_ID)

        val request = mockWebServer.takeRequest()
        val requestMethodName = request.path?.split("/")?.lastOrNull()
        val requestBody = request.body.readUtf8().decode()
        assertEquals("unpinChatMessage", requestMethodName)
        assertEquals("chat_id=$ANY_CHANNEL_USERNAME&message_id=$ANY_MESSAGE_ID", requestBody)
    }

    @Test
    fun `correct request on unpinChatMessage with channel username and no message id`() {
        givenASuccessfulUnpinChatMessageResponse()

        sut.unpinChatMessage(ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME), messageId = null)

        val request = mockWebServer.takeRequest()
        val requestMethodName = request.path?.split("/")?.lastOrNull()
        val requestBody = request.body.readUtf8().decode()
        assertEquals("unpinChatMessage", requestMethodName)
        assertEquals("chat_id=$ANY_CHANNEL_USERNAME", requestBody)
    }

    @Test
    fun `successful unpinChatMessage response is returned correctly`() {
        givenASuccessfulUnpinChatMessageResponse()

        val unpinChatMessageResult = sut.unpinChatMessage(
            ChatId.fromId(ANY_CHAT_ID),
            messageId = null
        ).getOrNull()

        assertEquals(false, unpinChatMessageResult)
    }

    private fun givenASuccessfulUnpinChatMessageResponse() {
        val unpinChatMessageResponse = """
            {
                "ok": true,
                "result": false
            }
        """.trimIndent()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(unpinChatMessageResponse)
        )
    }

    private companion object {
        const val ANY_CHAT_ID = 13513L
        const val ANY_CHANNEL_USERNAME = "@ruka"
        const val ANY_MESSAGE_ID = 12353151L
    }
}
