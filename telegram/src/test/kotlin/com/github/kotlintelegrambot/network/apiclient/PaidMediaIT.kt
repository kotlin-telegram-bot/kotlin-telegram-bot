package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.payments.InputPaidMedia
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

/**
 * Wire-format coverage for `sendPaidMedia` (Bot API 7.6).
 */
class PaidMediaIT : ApiClientIT() {
    @Test
    fun `sendPaidMedia posts chat_id, star_count and serialized media`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"ok":true,"result":{"message_id":1,"date":1700000000,"chat":{"id":1,"type":"private"}}}""",
            ),
        )

        sut.sendPaidMedia(
            chatId = ChatId.fromId(11L),
            starCount = 50,
            media = listOf(InputPaidMedia.Photo(media = "file-id")),
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/sendPaidMedia"))
        assertEquals(true, body.contains("chat_id=11"))
        assertEquals(true, body.contains("star_count=50"))
        assertEquals(true, body.contains("media="))
        assertEquals(true, body.contains("\"type\":\"photo\""))
        assertEquals(true, body.contains("\"media\":\"file-id\""))
    }
}
