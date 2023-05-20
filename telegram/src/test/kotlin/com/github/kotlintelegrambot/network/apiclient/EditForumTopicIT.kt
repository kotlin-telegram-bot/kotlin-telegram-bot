package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class EditForumTopicIT : ApiClientIT() {

    @Test
    fun `editForumTopic calls correct endpoint`() {
        givenAnyEditForumTopicResponse()

        sut.editForumTopic(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            messageThreadId = ANY_MESSAGE_THREAD_ID,
        )

        val request = mockWebServer.takeRequest()
        val expectedMethod = "POST"
        val expectedUrl = "/bot/editForumTopic"
        assertEquals(expectedMethod, request.method)
        assertEquals(expectedUrl, request.requestUrl?.encodedPath)
    }

    @Test
    fun `editForumTopic with chat id and mandatory params is properly sent`() {
        givenAnyEditForumTopicResponse()

        sut.editForumTopic(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            messageThreadId = ANY_MESSAGE_THREAD_ID,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID" +
            "&message_thread_id=$ANY_MESSAGE_THREAD_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `editForumTopic with channel username and mandatory params is properly sent`() {
        givenAnyEditForumTopicResponse()

        sut.editForumTopic(
            chatId = ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            messageThreadId = ANY_MESSAGE_THREAD_ID,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME" +
            "&message_thread_id=$ANY_MESSAGE_THREAD_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `editForumTopic with all the params is properly sent`() {
        givenAnyEditForumTopicResponse()

        sut.editForumTopic(
            chatId = ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            messageThreadId = ANY_MESSAGE_THREAD_ID,
            name = ANY_NAME,
            iconCustomEmojiId = ANY_ICON_CUSTOM_EMOJI_ID,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME" +
            "&message_thread_id=$ANY_MESSAGE_THREAD_ID" +
            "&name=$ANY_NAME" +
            "&icon_custom_emoji_id=$ANY_ICON_CUSTOM_EMOJI_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `editForumTopic response is returned correctly`() {
        givenAnyEditForumTopicResponse()

        val editForumTopicResult = sut.editForumTopic(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            messageThreadId = ANY_MESSAGE_THREAD_ID,
            name = ANY_NAME,
        )

        assertEquals(true, editForumTopicResult.getOrNull())
    }

    private fun givenAnyEditForumTopicResponse() {
        val editForumTopicResponse = """
            {
                "ok": true,
                "result": true
            }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(editForumTopicResponse)
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 235235235L
        const val ANY_CHANNEL_USERNAME = "@testtelegrambotapi"
        const val ANY_MESSAGE_THREAD_ID = 239429834L
        const val ANY_NAME = "Mucho texto"
        const val ANY_ICON_CUSTOM_EMOJI_ID = "smile"
    }
}
