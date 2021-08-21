package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.MessageEntity
import com.github.kotlintelegrambot.entities.MessageEntity.Type.ITALIC
import com.github.kotlintelegrambot.entities.ParseMode.MARKDOWN_V2
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.testutils.decode
import com.github.kotlintelegrambot.testutils.getFileAsStringFromResources
import com.github.kotlintelegrambot.testutils.getFileFromResources
import com.github.kotlintelegrambot.testutils.multipartBoundary
import com.google.gson.Gson
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class SendVoiceIT : ApiClientIT() {
    private fun String.normalizeLineEndings() = this.replace(Regex("\\r\\n?"), "\n")

    @Test
    fun `sendVoice with audioId`() {
        givenAnySendVoiceResponse()

        val sendVoice = sut.sendVoice(
            ChatId.fromId(ANY_CHAT_ID),
            TelegramFile.ByFileId(ANY_VOICE_FILE_ID),
            caption = CAPTION,
            parseMode = MARKDOWN_V2,
            captionEntities = CAPTION_ENTITIES,
            duration = DURATION,
            disableNotification = false,
            replyToMessageId = REPLY_TO_MESSAGE_ID,
            allowSendingWithoutReply = null,
            replyMarkup = REPLY_MARKUP
        )
        sendVoice.execute()

        val expectedRequestBody = "chat_id=$ANY_CHAT_ID" +
            "&voice=$ANY_VOICE_FILE_ID" +
            "&caption=$CAPTION" +
            "&parse_mode=${MARKDOWN_V2.modeName}" +
            "&caption_entities=${gson.toJson(CAPTION_ENTITIES)}" +
            "&duration=$DURATION" +
            "&disable_notification=false" +
            "&reply_to_message_id=$REPLY_TO_MESSAGE_ID" +
            "&reply_markup=${gson.toJson(REPLY_MARKUP)}"
        val request = mockWebServer.takeRequest()
        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `sendVoice with audio from file`() {
        givenAnySendVoiceResponse()

        val sendVoice = sut.sendVoice(
            ChatId.fromId(ANY_CHAT_ID),
            TelegramFile.ByFile(getFileFromResources<SendVoiceIT>(VOICE_FILENAME)),
            caption = CAPTION,
            parseMode = MARKDOWN_V2,
            captionEntities = CAPTION_ENTITIES,
            duration = DURATION,
            disableNotification = false,
            replyToMessageId = REPLY_TO_MESSAGE_ID,
            allowSendingWithoutReply = null,
            replyMarkup = REPLY_MARKUP
        )
        sendVoice.execute()

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendVoiceIT>("sendVoiceBody.txt"),
            multipartBoundary,
            String(getFileFromResources<SendVoiceIT>(VOICE_FILENAME).readBytes()),
            VOICE_FILENAME
        )
        assertEquals(expectedRequestBody.normalizeLineEndings(), requestBody.normalizeLineEndings())
    }

    @Test
    fun `sendVoice with audio from ByteArray`() {
        givenAnySendVoiceResponse()

        val sendVoice = sut.sendVoice(
            ChatId.fromId(ANY_CHAT_ID),
            TelegramFile.ByByteArray(getFileFromResources<SendVoiceIT>("short.ogg").readBytes()),
            caption = CAPTION,
            parseMode = MARKDOWN_V2,
            captionEntities = CAPTION_ENTITIES,
            duration = DURATION,
            disableNotification = false,
            replyToMessageId = REPLY_TO_MESSAGE_ID,
            allowSendingWithoutReply = null,
            replyMarkup = REPLY_MARKUP
        )
        sendVoice.execute()

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendVoiceIT>("sendVoiceBody.txt"),
            multipartBoundary,
            String(getFileFromResources<SendVoiceIT>("short.ogg").readBytes()),
            "voice"
        )
        assertEquals(expectedRequestBody.normalizeLineEndings(), requestBody.normalizeLineEndings())
    }

    private fun givenAnySendVoiceResponse() {
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                    {
                      "ok": true,
                      "result": {
                        "message_id": 846,
                        "from": {
                          "id": 1489255777,
                          "is_bot": true,
                          "first_name": "Bot",
                          "username": "Bot"
                        },
                        "chat": {
                          "id": 225423333,
                          "first_name": "John",
                          "last_name": "Doe",
                          "username": "JohnDoe",
                          "type": "private"
                        },
                        "date": 1607946576,
                        "voice": {
                          "duration": 18,
                          "mime_type": "audio/ogg",
                          "file_id": "AwACAgIAAxkDAAIDTl_XUVAqJaPQkKgo2Vbc7sTvKXuXAALbCwACH7yoSsWrILY2d2TyHgQ",
                          "file_unique_id": "AgAD2wsAAh-8qEo",
                          "file_size": 152576
                        },
                        "caption": "You have been rickrolled",
                        "caption_entities": [
                          {
                            "offset": 0,
                            "length": 3,
                            "type": "italic"
                          }
                        ]
                      }
                    }
                """.trimIndent()
            )
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        val gson = Gson()
        const val ANY_CHAT_ID = 3242424L
        const val REPLY_TO_MESSAGE_ID = 32235235L
        const val ANY_VOICE_FILE_ID =
            "AwACAgIAAxkDAAIDTl_XUVAqJaPQkKgo2Vbc7sTvKXuXAALbCwACH7yoSsWrILY2d2TyHgQ"
        const val CAPTION = "You have been rickrolled"
        const val DURATION = 18
        val CAPTION_ENTITIES = listOf(MessageEntity(ITALIC, 0, 3))
        val REPLY_MARKUP = InlineKeyboardMarkup.create(
            listOf(
                InlineKeyboardButton.CallbackData(
                    text = "Show alert",
                    callbackData = "showAlert"
                )
            )
        )
        const val VOICE_FILENAME = "short.ogg"
    }
}
