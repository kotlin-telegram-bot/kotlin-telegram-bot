package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InlineQueryResultsButtonTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes button with web_app`() {
        val json = """{"text":"Open Web App","web_app":{"url":"https://example.com/app"}}"""

        val button = gson.fromJson(json, InlineQueryResultsButton::class.java)

        assertThat(button.text).isEqualTo("Open Web App")
        assertThat(button.webApp?.url).isEqualTo("https://example.com/app")
        assertThat(button.startParameter).isNull()
    }

    @Test
    fun `deserializes button with start_parameter`() {
        val json = """{"text":"Start","start_parameter":"deep_link_payload"}"""

        val button = gson.fromJson(json, InlineQueryResultsButton::class.java)

        assertThat(button.text).isEqualTo("Start")
        assertThat(button.startParameter).isEqualTo("deep_link_payload")
        assertThat(button.webApp).isNull()
    }

    @Test
    fun `serializes button using snake_case fields`() {
        val button = InlineQueryResultsButton(
            text = "Go",
            startParameter = "payload",
        )

        val json = gson.toJson(button)

        assertThat(json).contains("\"text\":\"Go\"")
        assertThat(json).contains("\"start_parameter\":\"payload\"")
    }
}
