package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.testutils.decode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RestrictChatMembersIT : ApiClientIT() {

    @Test
    fun `restrictChatMember with no chat permissions sends the correct request`() =
        runTest {
            givenRestrictChatMembersSuccessResponse()

            sut.restrictChatMember(
                ChatId.fromId(ANY_CHAT_ID),
                ANY_USER_ID,
                ChatPermissions()
            )

            val request = mockWebServer.takeRequest()
            val expectedRequestBody = "chat_id=1241242&user_id=32523623&permissions={}"
            assertEquals(expectedRequestBody, request.body.readUtf8().decode())
        }

    @Test
    fun `restrictChatMember with one chat permission sends the correct request`() =
        runTest {
            givenRestrictChatMembersSuccessResponse()

            sut.restrictChatMember(
                ChatId.fromId(ANY_CHAT_ID),
                ANY_USER_ID,
                ChatPermissions(canSendMessages = false)
            )

            val request = mockWebServer.takeRequest()
            val expectedRequestBody = "chat_id=1241242&" +
                "user_id=32523623&" +
                "permissions={\"can_send_messages\":false}"
            assertEquals(expectedRequestBody, request.body.readUtf8().decode())
        }

    @Test
    fun `restrictChatMember with several chat permission sends the correct request`() = runTest {
        givenRestrictChatMembersSuccessResponse()

        sut.restrictChatMember(
            ChatId.fromId(ANY_CHAT_ID),
            ANY_USER_ID,
            ChatPermissions(
                canSendMessages = false,
                canSendPolls = true,
                canAddWebPagePreviews = false
            )
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=1241242&" +
            "user_id=32523623&" +
            "permissions={\"can_send_messages\":false," +
            "\"can_send_polls\":true," +
            "\"can_add_web_page_previews\":false}"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `restrictChatMember with until date sends the correct request`() = runTest {
        givenRestrictChatMembersSuccessResponse()

        sut.restrictChatMember(
            ChatId.fromId(ANY_CHAT_ID),
            ANY_USER_ID,
            ChatPermissions(),
            ANY_TIMESTAMP
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=1241242&" +
            "user_id=32523623&permissions={}&" +
            "until_date=132523523"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    private fun givenRestrictChatMembersSuccessResponse() {
        val restrictChatMembersResponseBody = """
            {
                "ok": true,
                "result": true 
            }
        """.trimIndent()
        val mockedSuccessResponse = MockResponse()
            .setResponseCode(200)
            .setBody(restrictChatMembersResponseBody)
        mockWebServer.enqueue(mockedSuccessResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 1241242L
        const val ANY_USER_ID = 32523623L
        const val ANY_TIMESTAMP = 132523523L
    }
}
