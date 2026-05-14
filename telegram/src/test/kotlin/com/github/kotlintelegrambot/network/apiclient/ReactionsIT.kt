package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.reaction.ReactionType
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

/**
 * Wire-format coverage for Bot API 7.0 / 10.0 message-reaction methods.
 */
class ReactionsIT : ApiClientIT() {
    @Test
    fun `setMessageReaction posts chat_id, message_id, serialized reaction and is_big`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.setMessageReaction(
            chatId = ChatId.fromId(11L),
            messageId = 7L,
            reaction = listOf(ReactionType.Emoji("👍")),
            isBig = true,
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/setMessageReaction"))
        assertEquals(true, body.contains("chat_id=11"))
        assertEquals(true, body.contains("message_id=7"))
        assertEquals(true, body.contains("reaction="))
        assertEquals(true, body.contains("\"type\":\"emoji\""))
        assertEquals(true, body.contains("is_big=true"))
    }

    @Test
    fun `deleteMessageReaction posts chat_id, message_id and optional user_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.deleteMessageReaction(chatId = ChatId.fromId(11L), messageId = 7L, userId = 42L)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/deleteMessageReaction"))
        assertEquals(true, body.contains("chat_id=11"))
        assertEquals(true, body.contains("message_id=7"))
        assertEquals(true, body.contains("user_id=42"))
    }

    @Test
    fun `deleteAllMessageReactions posts chat_id and message_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.deleteAllMessageReactions(chatId = ChatId.fromId(11L), messageId = 7L)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/deleteAllMessageReactions"))
        assertEquals(true, body.contains("chat_id=11"))
        assertEquals(true, body.contains("message_id=7"))
    }
}
