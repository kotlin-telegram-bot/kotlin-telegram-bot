package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.testutils.decode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SetChatPermissionsIT : ApiClientIT() {

    @Test
    fun `setChatPermissions with no chat permissions sends the correct request`() = runTest {
        givenSetChatPermissionsSuccessResponse()

        sut.setChatPermissions(ChatId.fromId(ANY_CHAT_ID), ChatPermissions())

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=1241242&permissions={}"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `setChatPermissions with one chat permission sends the correct request`() = runTest {
        givenSetChatPermissionsSuccessResponse()

        sut.setChatPermissions(
            ChatId.fromId(ANY_CHAT_ID),
            ChatPermissions(canSendMessages = false)
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=1241242&" +
            "permissions={\"can_send_messages\":false}"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `setChatPermissions with several chat permission sends the correct request`() = runTest {
        givenSetChatPermissionsSuccessResponse()

        sut.setChatPermissions(
            ChatId.fromId(ANY_CHAT_ID),
            ChatPermissions(
                canSendMessages = false,
                canSendPolls = true,
                canAddWebPagePreviews = false
            )
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=1241242&" +
            "permissions={\"can_send_messages\":false," +
            "\"can_send_polls\":true," +
            "\"can_add_web_page_previews\":false}"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    private fun givenSetChatPermissionsSuccessResponse() {
        val setChatPermissionsResponseBody = """
            {
                "ok": true,
                "result": true 
            }
        """.trimIndent()
        val mockedSuccessResponse = MockResponse()
            .setResponseCode(200)
            .setBody(setChatPermissionsResponseBody)
        mockWebServer.enqueue(mockedSuccessResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 1241242L
    }
}
