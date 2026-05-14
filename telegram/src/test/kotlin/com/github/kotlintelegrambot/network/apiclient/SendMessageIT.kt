package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ForceReplyMarkup
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.LinkPreviewOptions
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode.MARKDOWN
import com.github.kotlintelegrambot.entities.ReplyParameters
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.testutils.decode
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class SendMessageIT : ApiClientIT() {

    @Test
    fun `sendMessage with chat id and mandatory params is properly sent`() {
        givenAnySendMessageResponse()

        sut.sendMessage(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            text = ANY_TEXT,
            parseMode = null,
            disableNotification = null,
            protectContent = null,
            replyMarkup = null,
            messageThreadId = null,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID&text=$ANY_TEXT"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendMessage with channel username and mandatory params is properly sent`() {
        givenAnySendMessageResponse()

        sut.sendMessage(
            chatId = ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            text = ANY_TEXT,
            parseMode = null,
            disableNotification = null,
            protectContent = null,
            replyMarkup = null,
            messageThreadId = null,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME&text=$ANY_TEXT"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendMessage with all the params is properly sent`() {
        givenAnySendMessageResponse()

        sut.sendMessage(
            chatId = ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            text = ANY_TEXT,
            parseMode = MARKDOWN,
            disableNotification = true,
            protectContent = null,
            replyMarkup = ForceReplyMarkup(forceReply = false),
            messageThreadId = null,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME" +
            "&text=$ANY_TEXT" +
            "&parse_mode=Markdown" +
            "&disable_notification=true" +
            "&reply_markup={\"force_reply\":false}"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendMessage with inline keyboard is properly sent`() {
        givenAnySendMessageResponse()

        sut.sendMessage(
            chatId = ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            text = ANY_TEXT,
            parseMode = null,
            disableNotification = null,
            protectContent = null,
            replyMarkup = InlineKeyboardMarkup.create(
                listOf(
                    InlineKeyboardButton.Url(ANY_TEXT, ANY_URL),
                    InlineKeyboardButton.CallbackData(ANY_TEXT, ANY_TEXT),
                ),
                listOf(
                    InlineKeyboardButton.SwitchInlineQuery(ANY_TEXT, ANY_TEXT),
                    InlineKeyboardButton.SwitchInlineQueryCurrentChat(ANY_TEXT, ANY_TEXT),
                ),
            ),
            messageThreadId = null,
        )

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME" +
            "&text=$ANY_TEXT" +
            "&reply_markup={\"inline_keyboard\":[[" +
            "{\"text\":\"Mucho texto\",\"url\":\"https://www.github.com/vjgarciag96\"}," +
            "{\"text\":\"Mucho texto\",\"callback_data\":\"Mucho texto\"}" +
            "],[" +
            "{\"text\":\"Mucho texto\",\"switch_inline_query\":\"Mucho texto\"}," +
            "{\"text\":\"Mucho texto\",\"switch_inline_query_current_chat\":\"Mucho texto\"}" +
            "]]}"
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendMessage response is returned correctly`() {
        givenAnySendMessageResponse()

        val sendMessageResult = sut.sendMessage(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            text = ANY_TEXT,
            parseMode = null,
            disableNotification = null,
            protectContent = null,
            replyMarkup = null,
            messageThreadId = 1,
        )

        val expectedMessage = Message(
            messageId = 7,
            chat = Chat(
                id = -1001367429635,
                title = "[Channel] Test Telegram Bot API",
                username = "testtelegrambotapi",
                type = "channel",
            ),
            messageThreadId = 1,
            date = 1604158404,
            text = "I'm part of a test :)",
            authorSignature = "incognito",
        )
        assertEquals(expectedMessage, sendMessageResult.getOrNull())
    }

    @Test
    fun `sendMessage with reply_parameters (Bot API 7_0) serializes the JSON wrapper`() {
        givenAnySendMessageResponse()

        sut.sendMessage(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            text = ANY_TEXT,
            replyParameters = ReplyParameters(
                messageId = 42L,
                quote = "the quoted bit",
            ),
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, body.contains("reply_parameters="))
        assertEquals(true, body.contains("\"message_id\":42"))
        assertEquals(true, body.contains("\"quote\":\"the quoted bit\""))
    }

    @Test
    fun `sendMessage with link_preview_options (Bot API 7_0) serializes the JSON wrapper`() {
        givenAnySendMessageResponse()

        sut.sendMessage(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            text = ANY_TEXT,
            linkPreviewOptions = LinkPreviewOptions(
                isDisabled = false,
                url = "https://example.com",
                showAboveText = true,
            ),
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, body.contains("link_preview_options="))
        assertEquals(true, body.contains("\"is_disabled\":false"))
        assertEquals(true, body.contains("\"url\":\"https://example.com\""))
        assertEquals(true, body.contains("\"show_above_text\":true"))
    }

    @Test
    fun `sendMessage with business_connection_id and message_effect_id (Bot API 7_2 + 7_4)`() {
        givenAnySendMessageResponse()

        sut.sendMessage(
            chatId = ChatId.fromId(ANY_CHAT_ID),
            text = ANY_TEXT,
            businessConnectionId = "conn-abc",
            messageEffectId = "effect-123",
            allowPaidBroadcast = true,
        )

        val request = mockWebServer.takeRequest()
        val body = request.body.readUtf8().decode()
        assertEquals(true, body.contains("business_connection_id=conn-abc"))
        assertEquals(true, body.contains("message_effect_id=effect-123"))
        assertEquals(true, body.contains("allow_paid_broadcast=true"))
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
                    "message_thread_id": 1,
                    "date": 1604158404,
                    "text": "I'm part of a test :)",
                    "author_signature": "incognito"
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
        const val ANY_CHANNEL_USERNAME = "@testtelegrambotapi"
        const val ANY_MESSAGE_ID = 35235423L
        const val ANY_TEXT = "Mucho texto"
        const val ANY_URL = "https://www.github.com/vjgarciag96"
    }
}
