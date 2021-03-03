package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.network.ApiClient
import com.github.kotlintelegrambot.network.serialization.GsonFactory
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class ApiClientIT {

    protected val mockWebServer = MockWebServer()

    internal lateinit var sut: ApiClient

    @BeforeEach
    fun setUp() {
        mockWebServer.start()
        val webServerUrl = mockWebServer.url("")
        sut = ApiClient(
            token = "",
            apiUrl = webServerUrl.toString(),
            logLevel = LogLevel.None,
            gson = GsonFactory.createForApiClient()
        )
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
