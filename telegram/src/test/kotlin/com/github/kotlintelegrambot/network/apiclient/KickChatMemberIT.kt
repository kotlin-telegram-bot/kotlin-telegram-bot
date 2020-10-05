package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class KickChatMemberIT : ApiClientIT() {

    @Test
    fun `kickChatMember with no until date sends the correct request`() {
        givenKickChatMemberSuccessResponse()

        sut.kickChatMember(ANY_CHAT_ID, ANY_USER_ID).execute()

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&user_id=$ANY_USER_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `kickChatMember with until date sends the correct request`() {
        givenKickChatMemberSuccessResponse()

        sut.kickChatMember(
            ANY_CHAT_ID,
            ANY_USER_ID,
            ANY_TIMESTAMP
        ).execute()

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&user_id=$ANY_USER_ID&until_date=$ANY_TIMESTAMP"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    private fun givenKickChatMemberSuccessResponse() {
        val kickChatMemberResponseBody = """
            {
                "ok": true,
                "result": true 
            }
        """.trimIndent()
        val mockedSuccessResponse = MockResponse()
            .setResponseCode(200)
            .setBody(kickChatMemberResponseBody)
        mockWebServer.enqueue(mockedSuccessResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 35235234L
        const val ANY_USER_ID = 23512412L
        const val ANY_TIMESTAMP = 1235213523L
    }
}
