package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

/**
 * Bot API 7.0 batch methods: forwardMessages, copyMessages, deleteMessages.
 */
class BatchMessageIT : ApiClientIT() {

    @Test
    fun `forwardMessages serializes message_ids as a JSON array`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"ok":true,"result":[{"message_id":11},{"message_id":12}]}""",
            ),
        )

        sut.forwardMessages(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            fromChatId = ChatId.fromId(ANY_FROM_CHAT_ID),
            messageIds = listOf(1L, 2L),
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/forwardMessages"))
        assertEquals(true, body.contains("chat_id=$ANY_CHAT_ID"))
        assertEquals(true, body.contains("from_chat_id=$ANY_FROM_CHAT_ID"))
        assertEquals(true, body.contains("message_ids=[1,2]"))
    }

    @Test
    fun `copyMessages can request remove_caption`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":[]}"""),
        )

        sut.copyMessages(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            fromChatId = ChatId.fromId(ANY_FROM_CHAT_ID),
            messageIds = listOf(7L),
            removeCaption = true,
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/copyMessages"))
        assertEquals(true, body.contains("message_ids=[7]"))
        assertEquals(true, body.contains("remove_caption=true"))
    }

    @Test
    fun `deleteMessages posts the JSON array`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""),
        )

        sut.deleteMessages(chatId = ChatId.fromId(ANY_CHAT_ID), messageIds = listOf(1L, 2L, 3L))

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/deleteMessages"))
        assertEquals(true, body.contains("message_ids=[1,2,3]"))
    }

    @Test
    fun `getUserChatBoosts GETs chat_id and user_id`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":{"boosts":[]}}"""),
        )

        sut.getUserChatBoosts(chatId = ChatId.fromId(ANY_CHAT_ID), userId = 42L)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals(true, request.path?.contains("/getUserChatBoosts"))
        assertEquals(true, request.path?.contains("chat_id=$ANY_CHAT_ID"))
        assertEquals(true, request.path?.contains("user_id=42"))
    }

    private companion object {
        const val ANY_CHAT_ID = 235235235L
        const val ANY_FROM_CHAT_ID = 9876543210L
    }
}
