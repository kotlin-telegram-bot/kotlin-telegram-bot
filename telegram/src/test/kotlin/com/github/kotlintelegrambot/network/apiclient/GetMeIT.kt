package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetMeIT : ApiClientIT() {

    @Test
    fun `getMe response is correctly returned`() = runTest {
        givenAnyGetMeResponse()

        val getMeResult = sut.getMe()

        val expectedUser = User(
            id = 482352699,
            isBot = true,
            firstName = "Rick",
            username = "pickleman",
            canJoinGroups = true,
            canReadAllGroupMessages = false,
            supportsInlineQueries = false
        )
        assertEquals(expectedUser, getMeResult.getOrNull())
    }

    private fun givenAnyGetMeResponse() {
        val getMeResponse = """
            {
                "ok": true,
                "result": {
                    "id": 482352699,
                    "is_bot": true,
                    "first_name": "Rick",
                    "username": "pickleman",
                    "can_join_groups": true,
                    "can_read_all_group_messages": false,
                    "supports_inline_queries": false
                }
            }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(getMeResponse)
        mockWebServer.enqueue(mockedResponse)
    }
}
