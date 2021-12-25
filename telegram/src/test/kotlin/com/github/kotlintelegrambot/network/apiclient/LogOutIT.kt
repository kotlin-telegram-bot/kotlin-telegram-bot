package com.github.kotlintelegrambot.network.apiclient

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LogOutIT : ApiClientIT() {

    @Test
    fun `logOut response is correctly returned`(): Unit = runTest {
        givenAnyLogOutResponse()

        val logOutResponse = sut.logOut()

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
