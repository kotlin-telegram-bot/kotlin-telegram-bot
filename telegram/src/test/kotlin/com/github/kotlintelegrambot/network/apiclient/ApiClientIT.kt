package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.network.ApiClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class ApiClientIT {

    protected val mockWebServer = MockWebServer()

    protected lateinit var sut: ApiClient

    @BeforeEach
    fun setUp() {
        mockWebServer.start()
        val webServerUrl = mockWebServer.url("")
        sut = ApiClient(token = "", apiUrl = webServerUrl.toString(), logLevel = LogLevel.None)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
