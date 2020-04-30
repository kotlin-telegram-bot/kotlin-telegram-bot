package com.github.kotlintelegrambot.network

import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.getFileAsStringFromResources
import com.github.kotlintelegrambot.getFileFromResources
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ApiClientIT {

    private val mockWebServer = MockWebServer()

    private lateinit var sut: ApiClient

    @BeforeEach
    internal fun setUp() {
        mockWebServer.start()
        val webServerUrl = mockWebServer.url("")
        sut = ApiClient(token = "", apiUrl = webServerUrl.toString())
    }

    @AfterEach
    internal fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    internal fun `setWebhook without certificate`() {
        givenAnyMockedResponse()

        whenWebhookIsSetWithoutCertificate()

        thenSetWebhookRequestWithoutCertificateIsCorrect()
    }

    @Test
    internal fun `setWebhook with certificate as file`() {
        givenAnyMockedResponse()

        whenWebhookIsSetWithCertificateAsFile()

        thenSetWebhookRequestWithCertificateAsFileIsCorrect()
    }

    @Test
    internal fun `setWebhook with certificate as file id`() {
        givenAnyMockedResponse()

        whenWebhookIsSetWithCertificateAsFileId()

        thenSetWebhookRequestWithCertificateAsFileIdIsCorrect()
    }

    @Test
    internal fun `setWebhook with certificate as file url`() {
        givenAnyMockedResponse()

        whenWebhookIsSetWithCertificateAsFileUrl()

        thenSetWebhookRequestWithCertificateAsFileUrlIsCorrect()
    }

    private fun givenAnyMockedResponse() {
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{
                |"ok": true,
                |"result": true
                |}""".trimMargin()
            )
        mockWebServer.enqueue(mockedResponse)
    }

    private fun whenWebhookIsSetWithoutCertificate() {
        sut.setWebhook(url = ANY_WEBHOOK_URL).execute()
    }

    private fun whenWebhookIsSetWithCertificateAsFile() {
        sut.setWebhook(
            url = ANY_WEBHOOK_URL,
            certificate = TelegramFile.ByFile(getFileFromResources<ApiClientIT>("certificate.pem"))
        ).execute()
    }

    private fun whenWebhookIsSetWithCertificateAsFileId() {
        sut.setWebhook(
            url = ANY_WEBHOOK_URL,
            certificate = TelegramFile.ByFileId(ANY_FILE_ID)
        ).execute()
    }

    private fun whenWebhookIsSetWithCertificateAsFileUrl() {
        sut.setWebhook(
            url = ANY_WEBHOOK_URL,
            certificate = TelegramFile.ByUrl(ANY_FILE_URL)
        ).execute()
    }

    private fun thenSetWebhookRequestWithoutCertificateIsCorrect() {
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertEquals("url=https%3A%2F%2Fwebhook.telegram.io", requestBody)
    }

    private fun thenSetWebhookRequestWithCertificateAsFileIsCorrect() {
        val request = mockWebServer.takeRequest()
        val contentTypeHeader = request.getHeader("Content-Type")
        val multipartBoundaryEndIndex =
            contentTypeHeader?.indexOf(BOUNDARY_ATTR_NAME)?.plus(BOUNDARY_ATTR_NAME.length + 1)
        val multipartBoundary =
            contentTypeHeader?.substring(multipartBoundaryEndIndex ?: 0).orEmpty()
        val requestBody = request.body.readUtf8()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<ApiClientIT>("set_webhook_multipart_with_certificate.txt"),
            multipartBoundary,
            multipartBoundary,
            multipartBoundary
        )
        assertEquals(
            expectedRequestBody.replace("\r\n", "\n").replace('\r', '\n'),
            requestBody.replace("\r\n", "\n").replace('\r', '\n')
        )
    }

    private fun thenSetWebhookRequestWithCertificateAsFileIdIsCorrect() {
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertEquals(
            "url=https%3A%2F%2Fwebhook.telegram.io&certificate=rukaFileId1214",
            requestBody
        )
    }

    private fun thenSetWebhookRequestWithCertificateAsFileUrlIsCorrect() {
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertEquals(
            "url=https%3A%2F%2Fwebhook.telegram.io&certificate=https%3A%2F%2Fwww.mycert.es%2Fruka",
            requestBody
        )
    }

    private companion object {
        const val ANY_WEBHOOK_URL = "https://webhook.telegram.io"
        const val BOUNDARY_ATTR_NAME = "boundary"
        const val ANY_FILE_ID = "rukaFileId1214"
        const val ANY_FILE_URL = "https://www.mycert.es/ruka"
    }
}
