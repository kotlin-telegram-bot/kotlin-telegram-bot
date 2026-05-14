package com.github.kotlintelegrambot.entities.polls

import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InputPollOptionTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes minimal InputPollOption`() {
        val json = """{"text":"Option A"}"""

        val option = gson.fromJson(json, InputPollOption::class.java)

        assertThat(option.text).isEqualTo("Option A")
        assertThat(option.textParseMode).isNull()
        assertThat(option.textEntities).isNull()
    }

    @Test
    fun `deserializes InputPollOption with MarkdownV2 parse mode`() {
        val json = """{"text":"*bold*","text_parse_mode":"MarkdownV2"}"""

        val option = gson.fromJson(json, InputPollOption::class.java)

        assertThat(option.text).isEqualTo("*bold*")
        assertThat(option.textParseMode).isEqualTo(ParseMode.MARKDOWN_V2)
    }

    @Test
    fun `deserializes InputPollOption with HTML parse mode`() {
        val json = """{"text":"<b>x</b>","text_parse_mode":"HTML"}"""

        val option = gson.fromJson(json, InputPollOption::class.java)

        assertThat(option.textParseMode).isEqualTo(ParseMode.HTML)
    }

    @Test
    fun `deserializes InputPollOption with text_entities`() {
        val json = """
            {"text":"hello","text_entities":[{"type":"bold","offset":0,"length":5}]}
        """.trimIndent()

        val option = gson.fromJson(json, InputPollOption::class.java)

        assertThat(option.textEntities).hasSize(1)
        assertThat(option.textEntities?.first()?.offset).isEqualTo(0)
    }

    @Test
    fun `serializes InputPollOption with MarkdownV2 parse mode`() {
        val option = InputPollOption(text = "x", textParseMode = ParseMode.MARKDOWN_V2)

        val json = gson.toJson(option)

        assertThat(json).contains("\"text_parse_mode\":\"MarkdownV2\"")
    }
}
