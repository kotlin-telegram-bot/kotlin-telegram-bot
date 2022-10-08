package com.github.kotlintelegrambot.network.apiclient

import okhttp3.mockwebserver.MockResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CloseIT : ApiClientIT() {

    @Test
    fun `close response is correctly returned when true`() {
        givenTrueResponse()

        val closeResponse = sut.close().execute()

        assertThat(closeResponse.body()?.result).isTrue
    }

    @Test
    fun `close response is correctly returned when false`() {
        givenFalseResponse()

        val closeResponse = sut.close().execute()

        assertThat(closeResponse.body()?.result).isFalse()
    }

    private fun givenTrueResponse() {
        val trueResponse = """
            {
                "ok": true,
                "result": true
            }
        """.trimIndent()

        mockResponse(trueResponse)
    }

    private fun givenFalseResponse() {
        val falseResponse = """
            {
                "ok": true,
                "result": false
            }
        """.trimIndent()
        mockResponse(falseResponse)
    }

    private fun mockResponse(response: String) {
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(response)
        mockWebServer.enqueue(mockedResponse)
    }
}
