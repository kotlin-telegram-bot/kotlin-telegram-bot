package com.github.kotlintelegrambot.entities.checklists

import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InputChecklistTaskTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes InputChecklistTask with all fields`() {
        val json = """
            {"id":3,"text":"Buy milk","parse_mode":"MarkdownV2",
             "text_entities":[{"type":"bold","offset":0,"length":3}]}
        """.trimIndent()

        val task = gson.fromJson(json, InputChecklistTask::class.java)

        assertThat(task.id).isEqualTo(3)
        assertThat(task.text).isEqualTo("Buy milk")
        assertThat(task.parseMode).isEqualTo(ParseMode.MARKDOWN_V2)
        assertThat(task.textEntities).hasSize(1)
    }

    @Test
    fun `deserializes minimal InputChecklistTask`() {
        val json = """{"id":1,"text":"Item"}"""

        val task = gson.fromJson(json, InputChecklistTask::class.java)

        assertThat(task.id).isEqualTo(1)
        assertThat(task.text).isEqualTo("Item")
        assertThat(task.parseMode).isNull()
        assertThat(task.textEntities).isNull()
    }
}
