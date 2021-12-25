package com.github.kotlintelegrambot

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@OptIn(ExperimentalCoroutinesApi::class)
class BotIT {

    private val mockWebServer = MockWebServer()

    private lateinit var webServerUrl: String

    @BeforeEach
    fun setUp() {
        mockWebServer.start()
        webServerUrl = mockWebServer.url("").toString()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    internal fun `startWebhook throws an Exception when webhook config has not been set up`() {
        val sut = bot { token = ANY_BOT_TOKEN }

        val exception = assertThrows<IllegalStateException> {
            runBlocking { sut.startWebhook() }
        }

        assertEquals(
            "To start a webhook you need to configure it on bot set up. Check the `webhook` builder function",
            exception.message
        )
    }

    @Test
    internal fun `startWebhook returns false when webhook config has been set up but 'setWebhook' fails`() = runTest {
        val sut = bot {
            token = ANY_BOT_TOKEN
            apiUrl = webServerUrl
            webhook {
                url = ANY_WEBHOOK_URL
            }
        }
        givenSetWebhookFails()

        val startWebhookResult = sut.startWebhook()

        assertFalse(startWebhookResult)
    }

    @Test
    internal fun `startWebhook returns true when webhook config has been set up and 'setWebhook' succeeds`() = runTest {
        val sut = bot {
            token = ANY_BOT_TOKEN
            apiUrl = webServerUrl
            webhook {
                url = ANY_WEBHOOK_URL
            }
        }
        givenSetWebhookSucceeds()

        val startWebhookResult = sut.startWebhook()

        assertTrue(startWebhookResult)
    }

    @Test
    internal fun `stopWebhook throws an Exception when webhook config has not been set up`() {
        val sut = bot { token = ANY_BOT_TOKEN }

        val exception = assertThrows<IllegalStateException> {
            runBlocking { sut.stopWebhook() }
        }

        assertEquals(
            "To stop a webhook you need to configure it on bot set up. Check the `webhook` builder function",
            exception.message
        )
    }

    @Test
    internal fun `stopWebhook returns false when webhook config has been set up but 'deleteWebhook' fails`() = runTest {
        val sut = bot {
            token = ANY_BOT_TOKEN
            apiUrl = webServerUrl
            webhook {
                url = ANY_WEBHOOK_URL
            }
        }
        givenDeleteWebhookFails()

        val stopWebhookResult = sut.stopWebhook()

        assertFalse(stopWebhookResult)
    }

    @Test
    internal fun `stopWebhook returns true when webhook config has been set up and 'deleteWebhook' succeeds`() = runTest {
        val sut = bot {
            token = ANY_BOT_TOKEN
            apiUrl = webServerUrl
            webhook {
                url = ANY_WEBHOOK_URL
            }
        }
        givenDeleteWebhookSucceeds()

        val stopWebhookResult = sut.stopWebhook()

        assertTrue(stopWebhookResult)
    }

    private fun givenSetWebhookFails() {
        val errorResponse = MockResponse().setResponseCode(500)
        mockWebServer.enqueue(errorResponse)
    }

    private fun givenDeleteWebhookFails() {
        val errorResponse = MockResponse().setResponseCode(500)
        mockWebServer.enqueue(errorResponse)
    }

    private fun givenSetWebhookSucceeds() {
        val successResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{
                |"ok": true,
                |"result": true
                |}""".trimMargin()
            )
        mockWebServer.enqueue(successResponse)
    }

    private fun givenDeleteWebhookSucceeds() {
        val successResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{
                |"ok": true,
                |"result": true
                |}""".trimMargin()
            )
        mockWebServer.enqueue(successResponse)
    }

    private companion object {
        const val ANY_BOT_TOKEN = "1342142:asdad"
        const val ANY_WEBHOOK_URL = "https://ruka.io"
    }
}
