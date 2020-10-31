package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.ForceReplyMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode.MARKDOWN
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class SendMessageIT : ApiClientIT() {

    @Test
    fun `sendMessage with chat id and mandatory params is properly sent`() {
        givenAnySendMessageResponse()

        sut.sendMessage(
            chatId = ANY_CHAT_ID,
            text = ANY_TEXT,
            parseMode = null,
            disableWebPagePreview = null,
            disableNotification = null,
            replyToMessageId = null,
            replyMarkup = null
        ).execute()

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&text=$ANY_TEXT"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendMessage with channel username and mandatory params is properly sent`() {
        givenAnySendMessageResponse()

        sut.sendMessage(
            channelUsername = ANY_CHANNEL_USERNAME,
            text = ANY_TEXT,
            parseMode = null,
            disableWebPagePreview = null,
            disableNotification = null,
            replyToMessageId = null,
            replyMarkup = null
        ).execute()

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME&text=$ANY_TEXT"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendMessage with all the params is properly sent`() {
        givenAnySendMessageResponse()

        sut.sendMessage(
            channelUsername = ANY_CHANNEL_USERNAME,
            text = ANY_TEXT,
            parseMode = MARKDOWN,
            disableWebPagePreview = false,
            disableNotification = true,
            replyToMessageId = ANY_MESSAGE_ID,
            replyMarkup = ForceReplyMarkup(forceReply = false)
        ).execute()

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME" +
                "&text=$ANY_TEXT" +
                "&parse_mode=Markdown" +
                "&disable_web_page_preview=false" +
                "&disable_notification=true" +
                "&reply_to_message_id=$ANY_MESSAGE_ID" +
                "&reply_markup={\"force_reply\":false}"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendMessage response is returned correctly`() {
        givenAnySendMessageResponse()

        val sendMessageResult = sut.sendMessage(
            chatId = ANY_CHAT_ID,
            text = ANY_TEXT,
            parseMode = null,
            disableWebPagePreview = null,
            disableNotification = null,
            replyToMessageId = null,
            replyMarkup = null
        ).execute()

        val expectedMessage = Message(
            messageId = 7,
            chat = Chat(
                id = -1001367429635,
                title = "[Channel] Test Telegram Bot API",
                username = "testtelegrambotapi",
                type = "channel"
            ),
            date = 1604158404,
            text = "I'm part of a test :)"
        )
        assertEquals(expectedMessage, sendMessageResult.body()?.result)
    }

    private fun givenAnySendMessageResponse() {
        val sendMessageResponse = """
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
                    "text": "I'm part of a test :)"
                }
            }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(sendMessageResponse)
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 235235235L
        const val ANY_CHANNEL_USERNAME = "testtelegrambotapi"
        const val ANY_MESSAGE_ID = 35235423L
        const val ANY_TEXT = "Mucho texto"
    }
}
