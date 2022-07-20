package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.entities.files.Document
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaAudio
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaDocument
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaPhoto
import com.github.kotlintelegrambot.entities.inputmedia.MediaGroup
import com.github.kotlintelegrambot.entities.inputmedia.anyInputMediaPhoto
import com.github.kotlintelegrambot.entities.inputmedia.anyInputMediaVideo
import com.github.kotlintelegrambot.testutils.getFileAsStringFromResources
import com.github.kotlintelegrambot.testutils.getFileFromResources
import com.github.kotlintelegrambot.testutils.multipartBoundary
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class SendMediaGroupIT : ApiClientIT() {

    @Test
    fun `sendMediaGroup with media group composed by a video(fileUrl), a photo(fileId) and all the arguments`() {
        givenAnySendMediaGroupResponse()
        val mediaGroup = MediaGroup.from(
            anyInputMediaVideo(media = TelegramFile.ByUrl(ANY_VIDEO_URL)),
            anyInputMediaPhoto(media = TelegramFile.ByFileId(ANY_IMAGE_FILE_ID))
        )

        sut.sendMediaGroup(
            ChatId.fromId(ANY_CHAT_ID),
            mediaGroup,
            DISABLE_NOTIFICATION,
            REPLY_TO_MESSAGE_ID,
            ALLOW_SENDING_WITHOUT_REPLY
        )

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendMediaGroupIT>("sendMediaGroupRequestBody1.txt"),
            multipartBoundary
        ).trimIndent()
        assertEquals(expectedRequestBody, requestBody)
    }

    @Test
    fun `sendMediaGroup with media group composed by a video(fileUrl), a photo(fileId) and only the mandatory arguments`() {
        givenAnySendMediaGroupResponse()
        val mediaGroup = MediaGroup.from(
            anyInputMediaVideo(media = TelegramFile.ByUrl(ANY_VIDEO_URL)),
            anyInputMediaPhoto(media = TelegramFile.ByFileId(ANY_IMAGE_FILE_ID))
        )

        sut.sendMediaGroup(ChatId.fromId(ANY_CHAT_ID), mediaGroup)

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendMediaGroupIT>("sendMediaGroupRequestBody2.txt"),
            multipartBoundary
        ).trimIndent()
        assertEquals(expectedRequestBody, requestBody)
    }

    @Test
    fun `sendMediaGroup with media group composed by a photo(fileId) and a photo(file)`() {
        givenAnySendMediaGroupResponse()
        val mediaGroup = MediaGroup.from(
            anyInputMediaPhoto(media = TelegramFile.ByFileId(ANY_IMAGE_FILE_ID)),
            anyInputMediaPhoto(media = TelegramFile.ByFile(getFileFromResources<SendMediaGroupIT>("image.png")))
        )

        sut.sendMediaGroup(ChatId.fromId(ANY_CHAT_ID), mediaGroup)

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendMediaGroupIT>("sendMediaGroupRequestBody3.txt"),
            multipartBoundary
        ).trimIndent()
        assertEquals(expectedRequestBody, requestBody)
    }

    @Test
    fun `sendMediaGroup with media group composed by a video(url) with thumb and a photo(file)`() {
        givenAnySendMediaGroupResponse()
        val mediaGroup = MediaGroup.from(
            anyInputMediaVideo(
                media = TelegramFile.ByUrl(ANY_VIDEO_URL),
                thumb = TelegramFile.ByFile(getFileFromResources<SendMediaGroupIT>("thumb.jpeg"))
            ),
            anyInputMediaPhoto(media = TelegramFile.ByFile(getFileFromResources<SendMediaGroupIT>("image.png")))
        )

        sut.sendMediaGroup(ChatId.fromId(ANY_CHAT_ID), mediaGroup)

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendMediaGroupIT>("sendMediaGroupRequestBody4.txt"),
            multipartBoundary
        ).trimIndent()
        assertEquals(expectedRequestBody, requestBody)
    }

    @Test
    fun `correct request with a document album`() {
        givenAnySendMediaGroupResponse()
        val mediaGroup = MediaGroup.from(
            InputMediaDocument(TelegramFile.ByUrl("http://www.test.com/document.pdf")),
            InputMediaDocument(TelegramFile.ByUrl("http://www.test.com/document.pdf")),
        )

        sut.sendMediaGroup(ChatId.fromId(ANY_CHAT_ID), mediaGroup)

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendMediaGroupIT>("sendMediaGroupRequestBody5.txt"),
            multipartBoundary
        ).trimIndent()
        assertEquals(expectedRequestBody, requestBody)
    }

    @Test
    fun `correct response with a document album`() {
        givenADocumentAlbumSendMediaGroupResponse()
        val mediaGroup = MediaGroup.from(
            InputMediaDocument(TelegramFile.ByUrl("http://www.test.com/document.pdf")),
            InputMediaDocument(TelegramFile.ByUrl("http://www.test.com/document.pdf")),
        )

        val sendMediaGroupResult = sut.sendMediaGroup(ChatId.fromId(ANY_CHAT_ID), mediaGroup)

        val expectedSendMediaGroupResult = listOf(
            Message(
                messageId = 367,
                from = User(
                    id = 482352699,
                    isBot = true,
                    firstName = "test",
                    username = "testing",
                ),
                chat = Chat(
                    id = -10012869234005,
                    title = "Test Telegram Bot API",
                    username = "testlololololo",
                    type = "supergroup",
                ),
                date = 1613173782,
                mediaGroupId = "12905390261311724",
                document = Document(
                    fileId = "BQACAgQAAx0ETLWiZQACAW9gJxQWnL4BK94YIQ2mZNEXh60eVAACsQIAAqDZDFH8FekSdITd0R4E",
                    fileName = "c4611_sample_explain.pdf",
                    mimeType = "application/pdf",
                    fileUniqueId = "AgADsQIAAqDZDFE",
                    fileSize = 88226,
                )
            ),
            Message(
                messageId = 368,
                from = User(
                    id = 482352699,
                    isBot = true,
                    firstName = "test",
                    username = "testing",
                ),
                chat = Chat(
                    id = -10012869234005,
                    title = "Test Telegram Bot API",
                    username = "testlololololo",
                    type = "supergroup",
                ),
                date = 1613173782,
                mediaGroupId = "12905390261311724",
                document = Document(
                    fileId = "BQACAgQAAx0ETLWiZQACAXBgJxQWRBCyN_FrlAZG95HMSDNGvwACsQIAAqDZDFH8FekSdITd0R4E",
                    fileName = "c4611_sample_explain.pdf",
                    mimeType = "application/pdf",
                    fileUniqueId = "AgADsQIAAqDZDFE",
                    fileSize = 88226,
                )
            ),
        )
        assertEquals(expectedSendMediaGroupResult, sendMediaGroupResult.getOrNull())
    }

    @Test
    fun `correct request with an audio album`() {
        givenAnySendMediaGroupResponse()
        val mediaGroup = MediaGroup.from(
            InputMediaAudio(TelegramFile.ByUrl("http://www.test.com/audio.ogg")),
            InputMediaAudio(TelegramFile.ByUrl("http://www.test.com/audio.ogg")),
        )

        sut.sendMediaGroup(ChatId.fromId(ANY_CHAT_ID), mediaGroup)

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendMediaGroupIT>("sendMediaGroupRequestBody6.txt"),
            multipartBoundary
        ).trimIndent()
        assertEquals(expectedRequestBody, requestBody)
    }

    @Test
    fun `sendMediaGroup with media group composed by a document(file) with thumb and a photo(fileId)`() {
        givenAnySendMediaGroupResponse()
        val mediaGroup = MediaGroup.from(
            InputMediaDocument(TelegramFile.ByFile(getFileFromResources<SendMediaGroupIT>("doc.txt"))),
            InputMediaPhoto(TelegramFile.ByUrl(ANY_IMAGE_FILE_ID)),
        )

        sut.sendMediaGroup(ChatId.fromId(ANY_CHAT_ID), mediaGroup)

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendMediaGroupIT>("sendMediaGroupRequestBody7.txt"),
            multipartBoundary
        ).trimIndent()
        assertEquals(expectedRequestBody, requestBody)
    }

    @Test
    fun `correct response with an audio album`() {
        givenAnAudioAlbumSendMediaGroupResponse()
        val mediaGroup = MediaGroup.from(
            InputMediaAudio(TelegramFile.ByUrl("http://www.test.com/audio.ogg")),
            InputMediaAudio(TelegramFile.ByUrl("http://www.test.com/audio.ogg")),
        )

        val sendMediaGroupResult = sut.sendMediaGroup(ChatId.fromId(ANY_CHAT_ID), mediaGroup)

        val expectedSendMediaGroupResult = listOf(
            Message(
                messageId = 463,
                from = User(
                    id = 482352699,
                    isBot = true,
                    firstName = "test",
                    username = "testing",
                ),
                chat = Chat(
                    id = -1001286972005,
                    title = "Test Telegram Bot API",
                    username = "testlololololo",
                    type = "supergroup",
                ),
                date = 1613341529,
                mediaGroupId = "12906732234871572",
                document = Document(
                    fileName = "Example.ogg",
                    mimeType = "audio/ogg",
                    fileId = "BQACAgQAAx0ETLWiZQACAc9gKaNZiKV3XYG0a-D4pinoqu17SAAChqAAAjsdZAdeugzpG_zJth4E",
                    fileUniqueId = "AgADhqAAAjsdZAc",
                    fileSize = 105243,
                )
            ),
            Message(
                messageId = 464,
                from = User(
                    id = 482352699,
                    isBot = true,
                    firstName = "test",
                    username = "testing",
                ),
                chat = Chat(
                    id = -1001286972005,
                    title = "Test Telegram Bot API",
                    username = "testlololololo",
                    type = "supergroup",
                ),
                date = 1613341529,
                mediaGroupId = "12906732234871572",
                document = Document(
                    fileName = "Example.ogg",
                    mimeType = "audio/ogg",
                    fileId = "BQACAgQAAx0ETLWiZQACAdBgKaNZKeVLxQqsQ7Yyideg_XiiCAAChqAAAjsdZAdeugzpG_zJth4E",
                    fileUniqueId = "AgADhqAAAjsdZAc",
                    fileSize = 105243,
                )
            ),
        )
        assertEquals(expectedSendMediaGroupResult, sendMediaGroupResult.getOrNull())
    }

    private fun givenAnySendMediaGroupResponse() {
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                    {
                        "ok": true,
                        "result": [
                            {
                                "message_id": 367,
                                "from": {
                                    "id": 482352699,
                                    "is_bot": true,
                                    "first_name": "test",
                                    "username": "testing"
                                },
                                "chat": {
                                    "id": -10012869234005,
                                    "title": "Test Telegram Bot API",
                                    "username": "testlololololo",
                                    "type": "supergroup"
                                },
                                "date": 1613173782,
                                "media_group_id": "12905390261311724",
                                "document": {
                                    "file_name": "c4611_sample_explain.pdf",
                                    "mime_type": "application/pdf",
                                    "file_id": "BQACAgQAAx0ETLWiZQACAW9gJxQWnL4BK94YIQ2mZNEXh60eVAACsQIAAqDZDFH8FekSdITd0R4E",
                                    "file_unique_id": "AgADsQIAAqDZDFE",
                                    "file_size": 88226
                                }
                            },
                            {
                                "message_id": 368,
                                "from": {
                                    "id": 482352699,
                                    "is_bot": true,
                                    "first_name": "test",
                                    "username": "testing"
                                },
                                "chat": {
                                    "id": -10012869234005,
                                    "title": "Test Telegram Bot API",
                                    "username": "testlololololo",
                                    "type": "supergroup"
                                },
                                "date": 1613173782,
                                "media_group_id": "12905390261311724",
                                "document": {
                                    "file_name": "c4611_sample_explain.pdf",
                                    "mime_type": "application/pdf",
                                    "file_id": "BQACAgQAAx0ETLWiZQACAXBgJxQWRBCyN_FrlAZG95HMSDNGvwACsQIAAqDZDFH8FekSdITd0R4E",
                                    "file_unique_id": "AgADsQIAAqDZDFE",
                                    "file_size": 88226
                                }
                            }
                        ]
                    }
                """.trimIndent()
            )
        mockWebServer.enqueue(mockedResponse)
    }

    private fun givenADocumentAlbumSendMediaGroupResponse() {
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                    {
                        "ok": true,
                        "result": [
                            {
                                "message_id": 367,
                                "from": {
                                    "id": 482352699,
                                    "is_bot": true,
                                    "first_name": "test",
                                    "username": "testing"
                                },
                                "chat": {
                                    "id": -10012869234005,
                                    "title": "Test Telegram Bot API",
                                    "username": "testlololololo",
                                    "type": "supergroup"
                                },
                                "date": 1613173782,
                                "media_group_id": "12905390261311724",
                                "document": {
                                    "file_name": "c4611_sample_explain.pdf",
                                    "mime_type": "application/pdf",
                                    "file_id": "BQACAgQAAx0ETLWiZQACAW9gJxQWnL4BK94YIQ2mZNEXh60eVAACsQIAAqDZDFH8FekSdITd0R4E",
                                    "file_unique_id": "AgADsQIAAqDZDFE",
                                    "file_size": 88226
                                }
                            },
                            {
                                "message_id": 368,
                                "from": {
                                    "id": 482352699,
                                    "is_bot": true,
                                    "first_name": "test",
                                    "username": "testing"
                                },
                                "chat": {
                                    "id": -10012869234005,
                                    "title": "Test Telegram Bot API",
                                    "username": "testlololololo",
                                    "type": "supergroup"
                                },
                                "date": 1613173782,
                                "media_group_id": "12905390261311724",
                                "document": {
                                    "file_name": "c4611_sample_explain.pdf",
                                    "mime_type": "application/pdf",
                                    "file_id": "BQACAgQAAx0ETLWiZQACAXBgJxQWRBCyN_FrlAZG95HMSDNGvwACsQIAAqDZDFH8FekSdITd0R4E",
                                    "file_unique_id": "AgADsQIAAqDZDFE",
                                    "file_size": 88226
                                }
                            }
                        ]
                    }
                """.trimIndent()
            )
        mockWebServer.enqueue(mockedResponse)
    }

    private fun givenAnAudioAlbumSendMediaGroupResponse() {
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                {
                    "ok": true,
                    "result": [
                        {
                            "message_id": 463,
                            "from": {
                                "id": 482352699,
                                "is_bot": true,
                                "first_name": "test",
                                "username": "testing"
                            },
                            "chat": {
                                "id": -1001286972005,
                                "title": "Test Telegram Bot API",
                                "username": "testlololololo",
                                "type": "supergroup"
                            },
                            "date": 1613341529,
                            "media_group_id": "12906732234871572",
                            "document": {
                                "file_name": "Example.ogg",
                                "mime_type": "audio/ogg",
                                "file_id": "BQACAgQAAx0ETLWiZQACAc9gKaNZiKV3XYG0a-D4pinoqu17SAAChqAAAjsdZAdeugzpG_zJth4E",
                                "file_unique_id": "AgADhqAAAjsdZAc",
                                "file_size": 105243
                            }
                        },
                        {
                            "message_id": 464,
                            "from": {
                                "id": 482352699,
                                "is_bot": true,
                                "first_name": "test",
                                "username": "testing"
                            },
                            "chat": {
                                "id": -1001286972005,
                                "title": "Test Telegram Bot API",
                                "username": "testlololololo",
                                "type": "supergroup"
                            },
                            "date": 1613341529,
                            "media_group_id": "12906732234871572",
                            "document": {
                                "file_name": "Example.ogg",
                                "mime_type": "audio/ogg",
                                "file_id": "BQACAgQAAx0ETLWiZQACAdBgKaNZKeVLxQqsQ7Yyideg_XiiCAAChqAAAjsdZAdeugzpG_zJth4E",
                                "file_unique_id": "AgADhqAAAjsdZAc",
                                "file_size": 105243
                            }
                        }
                    ]
                }
                """.trimIndent()
            )
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 3242424L
        const val ANY_VIDEO_URL = "https://www.ghana.com/burying.mp4"
        const val ANY_IMAGE_FILE_ID = "fweo32r32nruka"
        const val DISABLE_NOTIFICATION = true
        const val REPLY_TO_MESSAGE_ID = 32235235L
        const val ALLOW_SENDING_WITHOUT_REPLY = true
    }
}
