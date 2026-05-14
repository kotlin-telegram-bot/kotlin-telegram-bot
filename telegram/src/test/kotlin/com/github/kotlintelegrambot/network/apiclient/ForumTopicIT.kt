package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

/**
 * Wire-format coverage for the Bot API 6.3 / 6.4 forum-topic methods.
 */
class ForumTopicIT : ApiClientIT() {
    @Test
    fun `createForumTopic posts chat_id, name and icon_color`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"ok":true,"result":{"message_thread_id":7,"name":"Bugs","icon_color":7322096}}""",
            ),
        )

        val result =
            sut.createForumTopic(
                chatId = ChatId.fromId(ANY_CHAT_ID),
                name = "Bugs",
                iconColor = 7322096,
            )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/createForumTopic"))
        assertEquals(true, body.contains("chat_id=$ANY_CHAT_ID"))
        assertEquals(true, body.contains("name=Bugs"))
        assertEquals(true, body.contains("icon_color=7322096"))
        assertEquals(7L, result.getOrNull()?.messageThreadId)
        assertEquals("Bugs", result.getOrNull()?.name)
    }

    @Test
    fun `editForumTopic posts chat_id, message_thread_id, name and icon_custom_emoji_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.editForumTopic(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            messageThreadId = 42L,
            name = "Bugs",
            iconCustomEmojiId = "ice-42",
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/editForumTopic"))
        assertEquals(true, body.contains("chat_id=$ANY_CHAT_ID"))
        assertEquals(true, body.contains("message_thread_id=42"))
        assertEquals(true, body.contains("name=Bugs"))
        assertEquals(true, body.contains("icon_custom_emoji_id=ice-42"))
    }

    @Test
    fun `closeForumTopic posts chat_id and message_thread_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.closeForumTopic(chatId = ChatId.fromId(ANY_CHAT_ID), messageThreadId = 42L)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/closeForumTopic"))
        assertEquals(true, body.contains("chat_id=$ANY_CHAT_ID"))
        assertEquals(true, body.contains("message_thread_id=42"))
    }

    @Test
    fun `reopenForumTopic posts chat_id and message_thread_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.reopenForumTopic(chatId = ChatId.fromId(ANY_CHAT_ID), messageThreadId = 42L)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/reopenForumTopic"))
        assertEquals(true, body.contains("chat_id=$ANY_CHAT_ID"))
        assertEquals(true, body.contains("message_thread_id=42"))
    }

    @Test
    fun `deleteForumTopic posts chat_id and message_thread_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.deleteForumTopic(chatId = ChatId.fromId(ANY_CHAT_ID), messageThreadId = 42L)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/deleteForumTopic"))
        assertEquals(true, body.contains("chat_id=$ANY_CHAT_ID"))
        assertEquals(true, body.contains("message_thread_id=42"))
    }

    @Test
    fun `unpinAllForumTopicMessages posts chat_id and message_thread_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.unpinAllForumTopicMessages(chatId = ChatId.fromId(ANY_CHAT_ID), messageThreadId = 42L)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/unpinAllForumTopicMessages"))
        assertEquals(true, body.contains("chat_id=$ANY_CHAT_ID"))
        assertEquals(true, body.contains("message_thread_id=42"))
    }

    @Test
    fun `getForumTopicIconStickers GETs without parameters`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":[]}"""))

        sut.getForumTopicIconStickers()

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals(true, request.path?.endsWith("/getForumTopicIconStickers"))
    }

    @Test
    fun `editGeneralForumTopic posts chat_id and name`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.editGeneralForumTopic(chatId = ChatId.fromId(ANY_CHAT_ID), name = "General-v2")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/editGeneralForumTopic"))
        assertEquals(true, body.contains("chat_id=$ANY_CHAT_ID"))
        assertEquals(true, body.contains("name=General-v2"))
    }

    @Test
    fun `closeGeneralForumTopic posts chat_id only`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.closeGeneralForumTopic(chatId = ChatId.fromId(ANY_CHAT_ID))

        val request = mockWebServer.takeRequest()
        assertEquals(true, request.path?.endsWith("/closeGeneralForumTopic"))
        assertEquals("chat_id=$ANY_CHAT_ID", request.body.readUtf8().decode())
    }

    @Test
    fun `reopenGeneralForumTopic posts chat_id only`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.reopenGeneralForumTopic(chatId = ChatId.fromId(ANY_CHAT_ID))

        val request = mockWebServer.takeRequest()
        assertEquals(true, request.path?.endsWith("/reopenGeneralForumTopic"))
        assertEquals("chat_id=$ANY_CHAT_ID", request.body.readUtf8().decode())
    }

    @Test
    fun `hideGeneralForumTopic posts chat_id only`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.hideGeneralForumTopic(chatId = ChatId.fromId(ANY_CHAT_ID))

        val request = mockWebServer.takeRequest()
        assertEquals(true, request.path?.endsWith("/hideGeneralForumTopic"))
        assertEquals("chat_id=$ANY_CHAT_ID", request.body.readUtf8().decode())
    }

    @Test
    fun `unhideGeneralForumTopic posts chat_id only`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.unhideGeneralForumTopic(chatId = ChatId.fromId(ANY_CHAT_ID))

        val request = mockWebServer.takeRequest()
        assertEquals(true, request.path?.endsWith("/unhideGeneralForumTopic"))
        assertEquals("chat_id=$ANY_CHAT_ID", request.body.readUtf8().decode())
    }

    private companion object {
        const val ANY_CHAT_ID = 235235235L
    }
}
