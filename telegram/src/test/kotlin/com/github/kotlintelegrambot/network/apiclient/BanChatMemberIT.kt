package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.decode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BanChatMemberIT : ApiClientIT() {

    @Test
    fun `banChatMember with no until date sends the correct request`() = runTest {
        givenBanChatMemberSuccessResponse()

        sut.banChatMember(ChatId.fromId(ANY_CHAT_ID), ANY_USER_ID)

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&user_id=$ANY_USER_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `banChatMember with until date sends the correct request`() = runTest {
        givenBanChatMemberSuccessResponse()

        sut.banChatMember(
            ChatId.fromId(ANY_CHAT_ID),
            ANY_USER_ID,
            ANY_TIMESTAMP
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&user_id=$ANY_USER_ID&until_date=$ANY_TIMESTAMP"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    private fun givenBanChatMemberSuccessResponse() {
        val banChatMemberResponseBody = """
            {
                "ok": true,
                "result": true 
            }
        """.trimIndent()
        val mockedSuccessResponse = MockResponse()
            .setResponseCode(200)
            .setBody(banChatMemberResponseBody)
        mockWebServer.enqueue(mockedSuccessResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 35235234L
        const val ANY_USER_ID = 23512412L
        const val ANY_TIMESTAMP = 1235213523L
    }
}
