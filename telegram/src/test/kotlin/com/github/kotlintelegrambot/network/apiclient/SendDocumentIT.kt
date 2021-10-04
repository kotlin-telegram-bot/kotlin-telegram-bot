package com.github.kotlintelegrambot.network.apiclient

import anyChat
import anyDocument
import anyMessage
import anyUser
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.MessageEntity
import com.github.kotlintelegrambot.entities.MessageEntity.Type.ITALIC
import com.github.kotlintelegrambot.entities.ParseMode.MARKDOWN_V2
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.network.Response
import com.github.kotlintelegrambot.testutils.decode
import com.github.kotlintelegrambot.testutils.getFileAsStringFromResources
import com.github.kotlintelegrambot.testutils.getFileFromResources
import com.github.kotlintelegrambot.testutils.multipartBoundary
import com.google.gson.Gson
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class SendDocumentIT : ApiClientIT() {
    private fun String.normalizeLineEndings() = this.replace(Regex("\\r\\n?"), "\n")

    @Test
    fun `#sendDocument using all params with documentId creates request correctly`() {
        givenAnySendDocumentResponse()

        sut.sendDocument(
            ChatId.fromId(ANY_CHAT_ID),
            TelegramFile.ByFileId(ANY_DOCUMENT_FILE_ID),
            caption = CAPTION,
            parseMode = MARKDOWN_V2,
            disableContentTypeDetection = false,
            disableNotification = false,
            replyToMessageId = REPLY_TO_MESSAGE_ID,
            allowSendingWithoutReply = null,
            replyMarkup = REPLY_MARKUP
        ).execute()

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID" +
            "&document=$ANY_DOCUMENT_FILE_ID" +
            "&caption=$CAPTION" +
            "&parse_mode=${MARKDOWN_V2.modeName}" +
            "&disable_content_type_detection=false" +
            "&disable_notification=false" +
            "&reply_to_message_id=$REPLY_TO_MESSAGE_ID" +
            "&reply_markup=${gson.toJson(REPLY_MARKUP)}"

        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `#sendDocument using all params with document file creates request correctly`() {
        givenAnySendDocumentResponse()

        sut.sendDocument(
            ChatId.fromId(ANY_CHAT_ID),
            TelegramFile.ByFile(getFileFromResources<SendDocumentIT>(DOCUMENT_FILE_NAME)),
            caption = CAPTION,
            parseMode = MARKDOWN_V2,
            disableContentTypeDetection = false,
            disableNotification = false,
            replyToMessageId = REPLY_TO_MESSAGE_ID,
            allowSendingWithoutReply = null,
            replyMarkup = REPLY_MARKUP
        ).execute()

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendDocumentIT>("sendDocumentBody.txt"),
            multipartBoundary,
            String(getFileFromResources<SendDocumentIT>(DOCUMENT_FILE_NAME).readBytes()),
            DOCUMENT_FILE_NAME
        )
        assertEquals(expectedRequestBody.normalizeLineEndings(), requestBody.normalizeLineEndings())
    }

    @Test
    fun `#sendDocument using all params with document from ByteArray creates request correctly`() {
        givenAnySendDocumentResponse()

        sut.sendDocument(
            ChatId.fromId(ANY_CHAT_ID),
            TelegramFile.ByByteArray(getFileFromResources<SendDocumentIT>(DOCUMENT_FILE_NAME).readBytes(), DOCUMENT_FILE_NAME),
            caption = CAPTION,
            parseMode = MARKDOWN_V2,
            disableContentTypeDetection = false,
            disableNotification = false,
            replyToMessageId = REPLY_TO_MESSAGE_ID,
            allowSendingWithoutReply = null,
            replyMarkup = REPLY_MARKUP,
            mimeType = "application/pdf"
        ).execute()

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendDocumentIT>("sendDocumentBody.txt"),
            multipartBoundary,
            String(getFileFromResources<SendDocumentIT>(DOCUMENT_FILE_NAME).readBytes()),
            DOCUMENT_FILE_NAME
        )
        assertEquals(expectedRequestBody.normalizeLineEndings(), requestBody.normalizeLineEndings())
    }

    @Test
    fun `#sendDocument using required params only with documentId creates request correctly`() {
        givenAnySendDocumentResponse()

        sut.sendDocument(
            ChatId.fromId(ANY_CHAT_ID),
            TelegramFile.ByFileId(ANY_DOCUMENT_FILE_ID)
        ).execute()

        val request = mockWebServer.takeRequest()
        val expectedRequestBody = "chat_id=$ANY_CHAT_ID" +
            "&document=$ANY_DOCUMENT_FILE_ID"

        assertEquals(expectedRequestBody, request.body.readUtf8().decode())
    }

    @Test
    fun `#sendDocument with required parameters returns response correctly`() {
        givenAnySendDocumentResponse()

        val sendDocument = sut.sendDocument(
            ChatId.fromId(ANY_CHAT_ID),
            TelegramFile.ByFileId(ANY_DOCUMENT_FILE_ID)
        ).execute()

        assertEquals(anyDocumentMessage.toString().trim(), sendDocument.body()?.result.toString().trim())
    }

    private fun givenAnySendDocumentResponse() {
        val sendDocumentResponse = Response<Message>(
            ok = true,
            result = anyDocumentMessage
        )

        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(gson.toJson(sendDocumentResponse))
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        val gson = Gson()
        const val ANY_CHAT_ID = 3242424L
        const val ANY_DOCUMENT_FILE_ID = "BQACAgEAAxkDAAN8YHey3OuhTvh-at..."
        const val DOCUMENT_FILE_NAME = "document.pdf"
        const val REPLY_TO_MESSAGE_ID = 32235235L
        const val CAPTION = "Caption"
        const val ANY_FILE_SIZE = 2897253
        val CAPTION_ENTITIES = listOf(MessageEntity(ITALIC, 0, 3))
        val REPLY_MARKUP = InlineKeyboardMarkup.create(
            listOf(
                InlineKeyboardButton.CallbackData(
                    text = "Show alert",
                    callbackData = "showAlert"
                )
            )
        )

        val anyDocumentMessage = anyMessage(
            from = anyUser(),
            chat = anyChat(
                id = ANY_CHAT_ID
            ),
            document = anyDocument(
                fileId = ANY_DOCUMENT_FILE_ID,
                fileName = DOCUMENT_FILE_NAME,
                mimeType = "application/pdf",
                fileSize = ANY_FILE_SIZE
            )
        )
    }
}
