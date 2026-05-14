package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

/**
 * Wire-format coverage for Bot API 8.2 verification methods.
 */
class VerificationIT : ApiClientIT() {
    @Test
    fun `verifyUser posts user_id and custom_description`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.verifyUser(userId = 42L, customDescription = "real user")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/verifyUser"))
        assertEquals(true, body.contains("user_id=42"))
        assertEquals(
            true,
            body.contains("custom_description=real user") ||
                body.contains("custom_description=real+user"),
        )
    }

    @Test
    fun `verifyChat posts chat_id and custom_description`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.verifyChat(chatId = ChatId.fromId(11L), customDescription = "verified chat")

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/verifyChat"))
        assertEquals(true, body.contains("chat_id=11"))
        assertEquals(
            true,
            body.contains("custom_description=verified chat") || body.contains("custom_description=verified+chat"),
        )
    }

    @Test
    fun `removeUserVerification posts user_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.removeUserVerification(userId = 42L)

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/removeUserVerification"))
        assertEquals("user_id=42", body)
    }

    @Test
    fun `removeChatVerification posts chat_id`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true,"result":true}"""))

        sut.removeChatVerification(chatId = ChatId.fromId(11L))

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, request.path?.endsWith("/removeChatVerification"))
        assertEquals("chat_id=11", body)
    }
}
