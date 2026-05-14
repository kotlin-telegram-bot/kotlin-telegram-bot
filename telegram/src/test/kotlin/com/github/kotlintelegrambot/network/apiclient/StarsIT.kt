package com.github.kotlintelegrambot.network.apiclient

import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

/**
 * Wire-format coverage for the Telegram Stars query methods (Bot API 7.5 / 9.1).
 */
class StarsIT : ApiClientIT() {

    @Test
    fun `getStarTransactions issues a GET with offset and limit`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":{"transactions":[]}}"""),
        )

        sut.getStarTransactions(offset = 0, limit = 100)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals(true, request.path?.contains("/getStarTransactions"))
        assertEquals(true, request.path?.contains("offset=0"))
        assertEquals(true, request.path?.contains("limit=100"))
    }

    @Test
    fun `getMyStarBalance issues a GET`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":{"amount":0}}"""))

        sut.getMyStarBalance()

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals(true, request.path?.contains("/getMyStarBalance"))
    }
}
