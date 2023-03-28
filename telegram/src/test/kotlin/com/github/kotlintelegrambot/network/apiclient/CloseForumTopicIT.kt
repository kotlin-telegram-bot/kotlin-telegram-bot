package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class CloseForumTopicIT : ApiClientIT() {

    @Test
    fun `closeForumTopic calls correct endpoint`() {
        givenAnyCloseForumTopicResponse()

        sut.closeForumTopic(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            messageThreadId = ANY_MESSAGE_THREAD_ID,
        )

        val request = mockWebServer.takeRequest()
        val expectedMethod = "POST"
        val expectedUrl = "/bot/closeForumTopic"
        assertEquals(expectedMethod, request.method)
        assertEquals(expectedUrl, request.requestUrl?.encodedPath)
    }

    @Test
    fun `closeForumTopic with chat id is properly sent`() {
        givenAnyCloseForumTopicResponse()

        sut.closeForumTopic(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            messageThreadId = ANY_MESSAGE_THREAD_ID,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID" +
            "&message_thread_id=$ANY_MESSAGE_THREAD_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `closeForumTopic with channel username is properly sent`() {
        givenAnyCloseForumTopicResponse()

        sut.closeForumTopic(
            chatId = ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            messageThreadId = ANY_MESSAGE_THREAD_ID,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME" +
            "&message_thread_id=$ANY_MESSAGE_THREAD_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `closeForumTopic response is returned correctly`() {
        givenAnyCloseForumTopicResponse()

        val closeForumTopicResult = sut.closeForumTopic(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            messageThreadId = ANY_MESSAGE_THREAD_ID,
        )

        assertEquals(true, closeForumTopicResult.getOrNull())
    }

    private fun givenAnyCloseForumTopicResponse() {
        val closeForumTopicResponse = """
            {
                "ok": true,
                "result": true
            }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(closeForumTopicResponse)
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 235235235L
        const val ANY_CHANNEL_USERNAME = "@testtelegrambotapi"
        const val ANY_MESSAGE_THREAD_ID = 239429834L
    }
}
