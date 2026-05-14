package com.github.kotlintelegrambot.entities.keyboard

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CopyTextButtonTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes CopyTextButton payload`() {
        val json = """{"text":"copy me"}"""

        val button = gson.fromJson(json, CopyTextButton::class.java)

        assertThat(button.text).isEqualTo("copy me")
    }

    @Test
    fun `serializes CopyTextButton payload`() {
        val button = CopyTextButton(text = "shareable")

        val json = gson.toJson(button)

        assertThat(json).isEqualTo("""{"text":"shareable"}""")
    }
}
