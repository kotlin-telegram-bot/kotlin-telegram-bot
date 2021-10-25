package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

private const val CHAT_ID = 12421434235435L
private const val FROM_CHAT_ID = -1001367429635L
private const val MESSAGE_ID = 7L

class ForwardMessageIT : ApiClientIT() {

    @Test
    fun `request with all parameters`() {
        givenAnyForwardMessageResponse()

        sut.forwardMessage(
            chatId = ChatId.fromId(CHAT_ID),
            fromChatId = ChatId.fromId(FROM_CHAT_ID),
            messageId = MESSAGE_ID,
            disableNotification = false,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$CHAT_ID" +
            "&from_chat_id=$FROM_CHAT_ID" +
            "&disable_notification=false" +
            "&message_id=$MESSAGE_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `request without disableNotification`() {
        givenAnyForwardMessageResponse()

        sut.forwardMessage(
            chatId = ChatId.fromId(CHAT_ID),
            fromChatId = ChatId.fromId(FROM_CHAT_ID),
            messageId = MESSAGE_ID,
            disableNotification = null,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$CHAT_ID" +
            "&from_chat_id=$FROM_CHAT_ID" +
            "&message_id=$MESSAGE_ID"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `message is properly returned on success`() {
        givenAnyForwardMessageResponse()

        val forwardMessageResult = sut.forwardMessage(
            chatId = ChatId.fromId(CHAT_ID),
            fromChatId = ChatId.fromId(FROM_CHAT_ID),
            messageId = MESSAGE_ID,
            disableNotification = false,
        )

        val expectedMessage = Message(
            messageId = MESSAGE_ID,
            chat = Chat(
                id = FROM_CHAT_ID,
                title = "[Channel] Test Telegram Bot API",
                username = "testtelegrambotapi",
                type = "channel"
            ),
            date = 1604158404,
            text = "I'm part of a test :)",
            authorSignature = "incognito",
        )
        assertEquals(expectedMessage, forwardMessageResult.getOrNull())
    }

    private fun givenAnyForwardMessageResponse() {
        val forwardMessageResponse = """
            {
                "ok": true,
                "result": {
                    "message_id": 7,
                    "chat": {
                        "id": -1001367429635,
                        "title": "[Channel] Test Telegram Bot API",
                        "username": "testtelegrambotapi",
                        "type": "channel"
                    },
                    "date": 1604158404,
                    "text": "I'm part of a test :)",
                    "author_signature": "incognito"
                }
            }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(forwardMessageResponse)
        mockWebServer.enqueue(mockedResponse)
    }
}
