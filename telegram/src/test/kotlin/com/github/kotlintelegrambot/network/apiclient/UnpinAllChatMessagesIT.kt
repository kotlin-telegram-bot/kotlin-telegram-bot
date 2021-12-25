package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.apiMethodName
import com.github.kotlintelegrambot.testutils.decodedBody
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UnpinAllChatMessagesIT : ApiClientIT() {

    @Test
    fun `correct request on unpinAllChatMessages with chat id`() = runTest {
        givenASuccessfulUnpinAllChatMessagesResponse()

        sut.unpinAllChatMessages(ChatId.fromId(ANY_CHAT_ID))

        val request = mockWebServer.takeRequest()
        assertEquals("unpinAllChatMessages", request.apiMethodName)
        assertEquals("chat_id=$ANY_CHAT_ID", request.decodedBody)
    }

    @Test
    fun `correct request on unpinAllChatMessages with channel username`() = runTest {
        givenASuccessfulUnpinAllChatMessagesResponse()

        sut.unpinAllChatMessages(ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME))

        val request = mockWebServer.takeRequest()
        assertEquals("unpinAllChatMessages", request.apiMethodName)
        assertEquals("chat_id=$ANY_CHANNEL_USERNAME", request.decodedBody)
    }

    @Test
    fun `successful unpinAllChatMessages response is returned correctly`() = runTest {
        givenASuccessfulUnpinAllChatMessagesResponse()

        val unpinChatMessageResult = sut.unpinAllChatMessages(
            ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME)
        ).getOrNull()

        assertEquals(false, unpinChatMessageResult)
    }

    private fun givenASuccessfulUnpinAllChatMessagesResponse() {
        val unpinAllChatMessagesResponse = """
            {
                "ok": true,
                "result": false
            }
        """.trimIndent()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(unpinAllChatMessagesResponse)
        )
    }

    private companion object {
        const val ANY_CHAT_ID = 1412412L
        const val ANY_CHANNEL_USERNAME = "@kotlin"
    }
}
