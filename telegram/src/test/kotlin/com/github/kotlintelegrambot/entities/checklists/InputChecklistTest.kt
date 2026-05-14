package com.github.kotlintelegrambot.entities.checklists

import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InputChecklistTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes InputChecklist with all fields`() {
        val json =
            """
            {"title":"Shopping","parse_mode":"HTML",
             "title_entities":[{"type":"italic","offset":0,"length":8}],
             "tasks":[{"id":1,"text":"Milk"},{"id":2,"text":"Eggs"}],
             "others_can_add_tasks":true,"others_can_mark_tasks_as_done":true}
            """.trimIndent()

        val input = gson.fromJson(json, InputChecklist::class.java)

        assertThat(input.title).isEqualTo("Shopping")
        assertThat(input.parseMode).isEqualTo(ParseMode.HTML)
        assertThat(input.titleEntities).hasSize(1)
        assertThat(input.tasks).hasSize(2)
        assertThat(input.othersCanAddTasks).isTrue
        assertThat(input.othersCanMarkTasksAsDone).isTrue
    }

    @Test
    fun `deserializes minimal InputChecklist`() {
        val json = """{"title":"Empty","tasks":[]}"""

        val input = gson.fromJson(json, InputChecklist::class.java)

        assertThat(input.title).isEqualTo("Empty")
        assertThat(input.tasks).isEmpty()
        assertThat(input.parseMode).isNull()
        assertThat(input.titleEntities).isNull()
        assertThat(input.othersCanAddTasks).isNull()
        assertThat(input.othersCanMarkTasksAsDone).isNull()
    }
}
