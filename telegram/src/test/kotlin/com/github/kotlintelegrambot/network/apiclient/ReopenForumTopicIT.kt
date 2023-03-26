package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class ReopenForumTopicIT : ApiClientIT() {

    @Test
    fun `reopenForumTopic calls correct endpoint`() {
        givenAnyReopenForumTopicResponse()

        sut.reopenForumTopic(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            messageThreadId = ANY_MESSAGE_THREAD_ID,
        )

        val request = mockWebServer.takeRequest()
        val expectedMethod = "POST"
        val expectedUrl = "/bot/reopenForumTopic"
        assertEquals(expectedMethod, request.method)
        assertEquals(expectedUrl, request.requestUrl?.encodedPath)
    }

    @Test
    fun `reopenForumTopic with chat id is properly sent`() {
        givenAnyReopenForumTopicResponse()

        sut.reopenForumTopic(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            messageThreadId = ANY_MESSAGE_THREAD_ID,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID" +
            "&message_thread_id=$ANY_MESSAGE_THREAD_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `reopenForumTopic with channel username is properly sent`() {
        givenAnyReopenForumTopicResponse()

        sut.reopenForumTopic(
            chatId = ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            messageThreadId = ANY_MESSAGE_THREAD_ID,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME" +
            "&message_thread_id=$ANY_MESSAGE_THREAD_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `reopenForumTopic response is returned correctly`() {
        givenAnyReopenForumTopicResponse()

        val reopenForumTopicResult = sut.reopenForumTopic(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            messageThreadId = ANY_MESSAGE_THREAD_ID,
        )

        assertEquals(true, reopenForumTopicResult.getOrNull())
    }

    private fun givenAnyReopenForumTopicResponse() {
        val reopenForumTopicResponse = """
            {
                "ok": true,
                "result": true
            }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(reopenForumTopicResponse)
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 235235235L
        const val ANY_CHANNEL_USERNAME = "@testtelegrambotapi"
        const val ANY_MESSAGE_THREAD_ID = 239429834L
    }
}
