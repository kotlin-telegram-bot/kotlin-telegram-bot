package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChatBackgroundTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes ChatBackground with chat_theme BackgroundType`() {
        val json = """{"type":{"type":"chat_theme","theme_name":"Tropical"}}"""

        val chatBackground = gson.fromJson(json, ChatBackground::class.java)

        assertThat(chatBackground.type).isInstanceOf(BackgroundType.ChatTheme::class.java)
        assertThat((chatBackground.type as BackgroundType.ChatTheme).themeName).isEqualTo("Tropical")
    }

    @Test
    fun `deserializes ChatBackground with fill BackgroundType`() {
        val json =
            """
            {"type":{"type":"fill","fill":{"type":"solid","color":12345},"dark_theme_dimming":20}}
            """.trimIndent()

        val chatBackground = gson.fromJson(json, ChatBackground::class.java)

        assertThat(chatBackground.type).isInstanceOf(BackgroundType.Fill::class.java)
        val fill = chatBackground.type as BackgroundType.Fill
        assertThat(fill.darkThemeDimming).isEqualTo(20)
        assertThat((fill.fill as BackgroundFill.Solid).color).isEqualTo(12345L)
    }
}
