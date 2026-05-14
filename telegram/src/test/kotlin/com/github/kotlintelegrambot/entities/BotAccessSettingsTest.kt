package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BotAccessSettingsTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes BotAccessSettings with expiration_date`() {
        val json = """{"allowed":true,"expiration_date":1700000000}"""

        val settings = gson.fromJson(json, BotAccessSettings::class.java)

        assertThat(settings.allowed).isTrue
        assertThat(settings.expirationDate).isEqualTo(1700000000L)
    }

    @Test
    fun `deserializes BotAccessSettings without expiration_date`() {
        val json = """{"allowed":false}"""

        val settings = gson.fromJson(json, BotAccessSettings::class.java)

        assertThat(settings.allowed).isFalse
        assertThat(settings.expirationDate).isNull()
    }
}
