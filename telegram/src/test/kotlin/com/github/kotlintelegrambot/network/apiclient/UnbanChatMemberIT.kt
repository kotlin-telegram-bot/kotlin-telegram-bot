package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.apiMethodName
import com.github.kotlintelegrambot.testutils.decodedBody
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class UnbanChatMemberIT : ApiClientIT() {

    @Test
    fun `correct request with mandatory parameters`() {
        givenASuccessfulResponse()

        sut.unbanChatMember(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            userId = ANY_USER_ID,
            onlyIfBanned = null
        )

        val request = mockWebServer.takeRequest()
        assertEquals("unbanChatMember", request.apiMethodName)
        assertEquals("chat_id=$ANY_CHAT_ID&user_id=$ANY_USER_ID", request.decodedBody)
    }

    @Test
    fun `correct request with all parameters`() {
        givenASuccessfulResponse()

        sut.unbanChatMember(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            userId = ANY_USER_ID,
            onlyIfBanned = true
        )

        val request = mockWebServer.takeRequest()
        assertEquals("unbanChatMember", request.apiMethodName)
        assertEquals(
            "chat_id=$ANY_CHAT_ID&user_id=$ANY_USER_ID&only_if_banned=true",
            request.decodedBody
        )
    }

    @Test
    fun `successful response is returned properly`() {
        givenASuccessfulResponse()

        val unbanChatMemberResult = sut.unbanChatMember(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            userId = ANY_USER_ID,
            onlyIfBanned = true
        )

        assertTrue(unbanChatMemberResult.get())
    }

    private fun givenASuccessfulResponse() {
        val responseBody = """
            {
                "ok": true,
                "result": true
            }
        """.trimIndent()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
        )
    }

    private companion object {
        const val ANY_CHAT_ID = 134124124L
        const val ANY_USER_ID = 32523451L
    }
}
