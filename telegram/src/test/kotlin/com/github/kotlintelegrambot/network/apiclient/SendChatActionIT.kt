package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.testutils.apiMethodName
import com.github.kotlintelegrambot.testutils.decodedBody
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SendChatActionIT : ApiClientIT() {

    @Test
    fun `send choose sticker chat action request`() {
        givenSuccessfulResponse()

        sut.sendChatAction(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            action = ChatAction.CHOOSE_STICKER
        )

        val request = mockWebServer.takeRequest()
        assertEquals("sendChatAction", request.apiMethodName)
        assertEquals("chat_id=$ANY_CHAT_ID&action=choose_sticker", request.decodedBody)
    }

    @Test
    fun `send choose sticker chat action success`() {
        givenSuccessfulResponse()

        val sendChatActionResult = sut.sendChatAction(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            action = ChatAction.CHOOSE_STICKER
        ).get()

        assertTrue(sendChatActionResult)
    }

    @Test
    fun `send choose sticker chat action failure`() {
        givenFailureResponse()

        val sendChatActionResult = sut.sendChatAction(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            action = ChatAction.CHOOSE_STICKER
        ).get()

        assertFalse(sendChatActionResult)
    }

    private fun givenSuccessfulResponse() {
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

    private fun givenFailureResponse() {
        val responseBody = """
            {
                "ok": true,
                "result": false 
            }
        """.trimIndent()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
        )
    }

    private companion object {
        private const val ANY_CHAT_ID = 35123523L
    }
}
