package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

/**
 * Wire-format coverage for Bot API 10.0 guest-mode methods.
 */
class GuestModeIT : ApiClientIT() {

    @Test
    fun `answerGuestQuery posts guest_query_id and optional text`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"ok":true,"result":{"chat":{"id":1,"type":"private"},"message_id":7}}""",
            ),
        )

        sut.answerGuestQuery(guestQueryId = "gq-abc", text = "hello")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/answerGuestQuery"))
        assertEquals(true, body.contains("guest_query_id=gq-abc"))
        assertEquals(true, body.contains("text=hello"))
    }
}
