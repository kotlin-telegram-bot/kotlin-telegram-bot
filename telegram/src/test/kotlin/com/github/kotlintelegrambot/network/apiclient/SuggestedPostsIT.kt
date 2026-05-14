package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

/**
 * Wire-format coverage for Bot API 9.2 suggested-post moderation methods.
 */
class SuggestedPostsIT : ApiClientIT() {
    @Test
    fun `approveSuggestedPost posts chat_id, message_id and send_date`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.approveSuggestedPost(chatId = ChatId.fromId(11L), messageId = 7L, sendDate = 1700000000L)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/approveSuggestedPost"))
        assertEquals(true, body.contains("chat_id=11"))
        assertEquals(true, body.contains("message_id=7"))
        assertEquals(true, body.contains("send_date=1700000000"))
    }

    @Test
    fun `declineSuggestedPost posts chat_id, message_id and comment`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.declineSuggestedPost(chatId = ChatId.fromId(11L), messageId = 7L, comment = "no")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/declineSuggestedPost"))
        assertEquals(true, body.contains("chat_id=11"))
        assertEquals(true, body.contains("message_id=7"))
        assertEquals(true, body.contains("comment=no"))
    }
}
