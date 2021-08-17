package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.testutils.getFileAsStringFromResources
import com.github.kotlintelegrambot.testutils.getFileFromResources
import com.github.kotlintelegrambot.testutils.multipartBoundary
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class SetWebhookIT : ApiClientIT() {

    @Test
    internal fun `setWebhook without certificate`() {
        givenAnyMockedResponse()

        whenWebhookIsSetWithoutCertificate()

        thenSetWebhookRequestWithoutCertificateIsCorrect()
    }

    @Test
    internal fun `setWebhook without certificate but with ip address`() {
        givenAnyMockedResponse()

        whenWebhookIsSetWithoutCertificateAndWithIpAddress()

        thenSetWebhookRequestWithoutCertificateAndWithIpAddressIsCorrect()
    }

    @Test
    internal fun `setWebhook with certificate as file`() {
        givenAnyMockedResponse()

        whenWebhookIsSetWithCertificateAsFile()

        thenSetWebhookRequestWithCertificateAsFileIsCorrect()
    }

    @Test
    internal fun `setWebhook with certificate as file and with ip address`() {
        givenAnyMockedResponse()

        whenWebhookIsSetWithCertificateAsFileAndWithIpAddress()

        thenSetWebhookRequestWithCertificateAsFileAndWithIpAddressIsCorrect()
    }

    @Test
    internal fun `setWebhook with certificate as file id`() {
        givenAnyMockedResponse()

        whenWebhookIsSetWithCertificateAsFileId()

        thenSetWebhookRequestWithCertificateAsFileIdIsCorrect()
    }

    @Test
    internal fun `setWebhook with certificate as file id and with ip address`() {
        givenAnyMockedResponse()

        whenWebhookIsSetWithCertificateAsFileIdAndWithIpAddress()

        thenSetWebhookRequestWithCertificateAsFileIdAndWithIpAddressIsCorrect()
    }

    @Test
    internal fun `setWebhook with certificate as file url`() {
        givenAnyMockedResponse()

        whenWebhookIsSetWithCertificateAsFileUrl()

        thenSetWebhookRequestWithCertificateAsFileUrlIsCorrect()
    }

    @Test
    internal fun `setWebhook with certificate as file url and with ip address`() {
        givenAnyMockedResponse()

        whenWebhookIsSetWithCertificateAsFileUrlAndWithIpAddress()

        thenSetWebhookRequestWithCertificateAsFileUrlAndWithIpAddressIsCorrect()
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

    private fun whenWebhookIsSetWithoutCertificateAndWithIpAddress() {
        sut.setWebhook(url = ANY_WEBHOOK_URL, ipAddress = ANY_IP_ADDRESS).execute()
    }

    private fun whenWebhookIsSetWithCertificateAsFile() {
        sut.setWebhook(
            url = ANY_WEBHOOK_URL,
            certificate = TelegramFile.ByFile(getFileFromResources<SetWebhookIT>("certificate.pem"))
        ).execute()
    }

    private fun whenWebhookIsSetWithCertificateAsFileAndWithIpAddress() {
        sut.setWebhook(
            url = ANY_WEBHOOK_URL,
            certificate = TelegramFile.ByFile(getFileFromResources<SetWebhookIT>("certificate.pem")),
            ipAddress = ANY_IP_ADDRESS
        ).execute()
    }

    private fun whenWebhookIsSetWithCertificateAsFileId() {
        sut.setWebhook(
            url = ANY_WEBHOOK_URL,
            certificate = TelegramFile.ByFileId(ANY_FILE_ID)
        ).execute()
    }

    private fun whenWebhookIsSetWithCertificateAsFileIdAndWithIpAddress() {
        sut.setWebhook(
            url = ANY_WEBHOOK_URL,
            certificate = TelegramFile.ByFileId(ANY_FILE_ID),
            ipAddress = ANY_IP_ADDRESS
        ).execute()
    }

    private fun whenWebhookIsSetWithCertificateAsFileUrl() {
        sut.setWebhook(
            url = ANY_WEBHOOK_URL,
            certificate = TelegramFile.ByUrl(ANY_FILE_URL)
        ).execute()
    }

    private fun whenWebhookIsSetWithCertificateAsFileUrlAndWithIpAddress() {
        sut.setWebhook(
            url = ANY_WEBHOOK_URL,
            certificate = TelegramFile.ByUrl(ANY_FILE_URL),
            ipAddress = ANY_IP_ADDRESS
        ).execute()
    }

    private fun thenSetWebhookRequestWithoutCertificateIsCorrect() {
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertEquals("url=https%3A%2F%2Fwebhook.telegram.io", requestBody)
    }

    private fun thenSetWebhookRequestWithoutCertificateAndWithIpAddressIsCorrect() {
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertEquals("url=https%3A%2F%2Fwebhook.telegram.io&ip_address=$ANY_IP_ADDRESS", requestBody)
    }

    private fun thenSetWebhookRequestWithCertificateAsFileIsCorrect() {
        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SetWebhookIT>("setWebhookMultipartWithCertificateRequestBody.txt"),
            multipartBoundary
        ).trimIndent()
        assertEquals(expectedRequestBody, requestBody)
    }

    private fun thenSetWebhookRequestWithCertificateAsFileAndWithIpAddressIsCorrect() {
        val request = mockWebServer.takeRequest()
        val multipartBoundary = request.multipartBoundary
        val requestBody = request.body.readUtf8().trimIndent()
        val expectedRequestBody = String.format(
            getFileAsStringFromResources<SetWebhookIT>("setWebhookMultipartWithCertificateAndWithIpAddressRequestBody.txt"),
            multipartBoundary,
            ANY_IP_ADDRESS
        ).trimIndent()
        assertEquals(expectedRequestBody, requestBody)
    }

    private fun thenSetWebhookRequestWithCertificateAsFileIdIsCorrect() {
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertEquals(
            "url=https%3A%2F%2Fwebhook.telegram.io&certificate=rukaFileId1214",
            requestBody
        )
    }

    private fun thenSetWebhookRequestWithCertificateAsFileIdAndWithIpAddressIsCorrect() {
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertEquals(
            "url=https%3A%2F%2Fwebhook.telegram.io&certificate=rukaFileId1214&ip_address=$ANY_IP_ADDRESS",
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

    private fun thenSetWebhookRequestWithCertificateAsFileUrlAndWithIpAddressIsCorrect() {
        val request = mockWebServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertEquals(
            "url=https%3A%2F%2Fwebhook.telegram.io&certificate=https%3A%2F%2Fwww.mycert.es%2Fruka&ip_address=$ANY_IP_ADDRESS",
            requestBody
        )
    }

    private companion object {
        const val ANY_WEBHOOK_URL = "https://webhook.telegram.io"
        const val ANY_FILE_ID = "rukaFileId1214"
        const val ANY_FILE_URL = "https://www.mycert.es/ruka"
        const val ANY_IP_ADDRESS = "214.88.209.113"
    }
}
