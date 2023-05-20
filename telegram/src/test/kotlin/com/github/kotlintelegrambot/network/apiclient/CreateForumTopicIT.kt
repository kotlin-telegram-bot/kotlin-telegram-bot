package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ForumTopic
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class CreateForumTopicIT : ApiClientIT() {

    @Test
    fun `createForumTopic calls correct endpoint`() {
        givenAnyCreateForumTopicResponse()

        sut.createForumTopic(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            name = ANY_NAME,
        )

        val request = mockWebServer.takeRequest()
        val expectedMethod = "POST"
        val expectedUrl = "/bot/createForumTopic"
        assertEquals(expectedMethod, request.method)
        assertEquals(expectedUrl, request.requestUrl?.encodedPath)
    }

    @Test
    fun `createForumTopic with chat id and mandatory params is properly sent`() {
        givenAnyCreateForumTopicResponse()

        sut.createForumTopic(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            name = ANY_NAME,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&name=$ANY_NAME"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `createForumTopic with channel username and mandatory params is properly sent`() {
        givenAnyCreateForumTopicResponse()

        sut.createForumTopic(
            chatId = ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            name = ANY_NAME,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME&name=$ANY_NAME"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `createForumTopic with all the params is properly sent`() {
        givenAnyCreateForumTopicResponse()

        sut.createForumTopic(
            chatId = ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            name = ANY_NAME,
            iconColor = ANY_ICON_COLOR,
            iconCustomEmojiId = ANY_ICON_CUSTOM_EMOJI_ID,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME" +
            "&name=$ANY_NAME" +
            "&icon_color=$ANY_ICON_COLOR&" +
            "icon_custom_emoji_id=$ANY_ICON_CUSTOM_EMOJI_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `createForumTopic response is returned correctly`() {
        givenAnyCreateForumTopicResponse()

        val createForumTopicResult = sut.createForumTopic(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            name = ANY_NAME,
        )

        val expectedForumTopic = ForumTopic(
            messageThreadId = 7,
            name = ANY_NAME,
            iconColor = 45,
            iconCustomEmojiId = "smile",
        )
        assertEquals(expectedForumTopic, createForumTopicResult.getOrNull())
    }

    private fun givenAnyCreateForumTopicResponse() {
        val createForumTopicResponse = """
            {
                "ok": true,
                "result": {
                    "message_thread_id": 7,
                    "name": "Mucho texto",
                    "icon_color": 45,
                    "icon_custom_emoji_id": "smile"
                }
            }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(createForumTopicResponse)
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 235235235L
        const val ANY_CHANNEL_USERNAME = "@testtelegrambotapi"
        const val ANY_NAME = "Mucho texto"
        const val ANY_ICON_COLOR = 12345
        const val ANY_ICON_CUSTOM_EMOJI_ID = "smile"
    }
}
