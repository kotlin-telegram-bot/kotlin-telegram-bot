package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TextQuoteTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes TextQuote from JSON`() {
        val json = """
            {"text":"hello world","position":0,"is_manual":true,"entities":[{"type":"bold","offset":0,"length":5}]}
        """.trimIndent()

        val quote = gson.fromJson(json, TextQuote::class.java)

        assertThat(quote.text).isEqualTo("hello world")
        assertThat(quote.position).isEqualTo(0)
        assertThat(quote.isManual).isTrue()
        assertThat(quote.entities).hasSize(1)
    }

    @Test
    fun `deserializes TextQuote with only required fields`() {
        val json = """{"text":"hi","position":3}"""

        val quote = gson.fromJson(json, TextQuote::class.java)

        assertThat(quote.text).isEqualTo("hi")
        assertThat(quote.position).isEqualTo(3)
        assertThat(quote.isManual).isNull()
        assertThat(quote.entities).isNull()
    }
}
