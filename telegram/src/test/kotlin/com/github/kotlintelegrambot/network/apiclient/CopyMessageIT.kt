package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.MessageEntity
import com.github.kotlintelegrambot.entities.MessageEntity.Type.ITALIC
import com.github.kotlintelegrambot.entities.MessageId
import com.github.kotlintelegrambot.entities.ParseMode.MARKDOWN_V2
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton.CallbackData
import com.github.kotlintelegrambot.testutils.decode
import com.google.gson.Gson
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class CopyMessageIT : ApiClientIT() {

    @Test
    fun `copy message with all parameters`() {
        givenAnyCopyMessageResponse()

        sut.copyMessage(
            ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            ChatId.fromId(ANY_CHAT_ID),
            messageId = ANY_MESSAGE_ID,
            caption = ANY_CAPTION,
            parseMode = MARKDOWN_V2,
            captionEntities = CAPTION_ENTITIES,
            disableNotification = true,
            replyToMessageId = REPLY_TO_MESSAGE_ID,
            allowSendingWithoutReply = true,
            replyMarkup = REPLY_MARKUP
        ).execute()

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHANNEL_USERNAME" +
            "&from_chat_id=$ANY_CHAT_ID" +
            "&message_id=$ANY_MESSAGE_ID" +
            "&caption=$ANY_CAPTION" +
            "&parse_mode=${MARKDOWN_V2.modeName}" +
            "&caption_entities=${gson.toJson(CAPTION_ENTITIES)}" +
            "&disable_notification=true" +
            "&reply_to_message_id=$REPLY_TO_MESSAGE_ID" +
            "&allow_sending_without_reply=true" +
            "&reply_markup=${gson.toJson(REPLY_MARKUP)}"

        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `copyMessage response is returned correctly`() {
        givenAnyCopyMessageResponse()

        val copyMessageResponse = sut.copyMessage(
            ChatId.fromId(ANY_CHAT_ID),
            ChatId.fromChannelUsername(ANY_CHANNEL_USERNAME),
            ANY_MESSAGE_ID
        ).execute()

        val expectedMessageId = MessageId(messageId = ANY_RESULT_MESSAGE_ID)
        assertEquals(expectedMessageId, copyMessageResponse.body()?.result)
    }

    private fun givenAnyCopyMessageResponse() {
        val copyMessageResponse = """
            {"ok":true,"result":{"message_id":$ANY_RESULT_MESSAGE_ID}}
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(copyMessageResponse)
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        val gson = Gson()
        const val ANY_CHAT_ID = 12412342L
        const val ANY_CHANNEL_USERNAME = "@polly"
        const val ANY_CAPTION = "was geht ab?"
        const val REPLY_TO_MESSAGE_ID = 32235235L
        const val ANY_MESSAGE_ID = 2314314L
        const val ANY_RESULT_MESSAGE_ID = 123L
        val CAPTION_ENTITIES = arrayListOf(MessageEntity(ITALIC, 0, 10))
        val REPLY_MARKUP = InlineKeyboardMarkup.createSingleButton(CallbackData(text = "wow", callbackData = "such callback"))
    }
}
