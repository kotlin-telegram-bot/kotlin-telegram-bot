package com.github.kotlintelegrambot.network.apiclient

import okhttp3.mockwebserver.MockResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LogOutIT : ApiClientIT() {

    @Test
    fun `logOut response is correctly returned`() {
        givenAnyLogOutResponse()

        val logOutResponse = sut.logOut().execute()

        assertThat(logOutResponse.body()?.result).isTrue
    }

    private fun givenAnyLogOutResponse() {
        val logOutResponse = """
            {
                "ok": true,
                "result": true
            }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(logOutResponse)
        mockWebServer.enqueue(mockedResponse)
    }
}
