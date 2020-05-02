package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.TelegramFile
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

        sut.sendMediaGroup(ANY_CHAT_ID, mediaGroup, DISABLE_NOTIFICATION, REPLY_TO_MESSAGE_ID).execute()

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendMediaGroupIT>("sendMediaGroupRequestBody1.txt"),
            multipartBoundary
        )
        assertEquals(expectedRequestBody, requestBody)
    }

    @Test
    fun `sendMediaGroup with media group composed by a video(fileUrl), a photo(fileId) and only the mandatory arguments`() {
        givenAnySendMediaGroupResponse()
        val mediaGroup = MediaGroup.from(
            anyInputMediaVideo(media = TelegramFile.ByUrl(ANY_VIDEO_URL)),
            anyInputMediaPhoto(media = TelegramFile.ByFileId(ANY_IMAGE_FILE_ID))
        )

        sut.sendMediaGroup(ANY_CHAT_ID, mediaGroup).execute()

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendMediaGroupIT>("sendMediaGroupRequestBody2.txt"),
            multipartBoundary
        )
        assertEquals(expectedRequestBody, requestBody)
    }

    @Test
    fun `sendMediaGroup with media group composed by a photo(fileId) and a photo(file)`() {
        givenAnySendMediaGroupResponse()
        val mediaGroup = MediaGroup.from(
            anyInputMediaPhoto(media = TelegramFile.ByFileId(ANY_IMAGE_FILE_ID)),
            anyInputMediaPhoto(media = TelegramFile.ByFile(getFileFromResources<SendMediaGroupIT>("image.png")))
        )

        sut.sendMediaGroup(ANY_CHAT_ID, mediaGroup).execute()

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendMediaGroupIT>("sendMediaGroupRequestBody3.txt"),
            multipartBoundary
        )
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

        sut.sendMediaGroup(ANY_CHAT_ID, mediaGroup).execute()

        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SendMediaGroupIT>("sendMediaGroupRequestBody4.txt"),
            multipartBoundary
        )
        assertEquals(expectedRequestBody, requestBody)
    }

    private fun givenAnySendMediaGroupResponse() {
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{
                |"ok": true,
                |"result": []
                |}""".trimMargin()
            )
        mockWebServer.enqueue(mockedResponse)
    }

    private companion object {
        const val ANY_CHAT_ID = 3242424L
        const val ANY_VIDEO_URL = "https://www.ghana.com/burying.mp4"
        const val ANY_IMAGE_FILE_ID = "fweo32r32nruka"
        const val DISABLE_NOTIFICATION = true
        const val REPLY_TO_MESSAGE_ID = 32235235L
    }
}
