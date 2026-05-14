package com.github.kotlintelegrambot.entities.checklists

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChecklistTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes Checklist with all fields`() {
        val json =
            """
            {
              "title":"Groceries",
              "title_entities":[{"type":"bold","offset":0,"length":9}],
              "tasks":[
                {"id":1,"text":"Milk"},
                {"id":2,"text":"Bread","completion_date":1700000000,
                 "completed_by_user":{"id":42,"is_bot":false,"first_name":"Alice"}}
              ],
              "others_can_add_tasks":true,
              "others_can_mark_tasks_as_done":false
            }
            """.trimIndent()

        val checklist = gson.fromJson(json, Checklist::class.java)

        assertThat(checklist.title).isEqualTo("Groceries")
        assertThat(checklist.titleEntities).hasSize(1)
        assertThat(checklist.tasks).hasSize(2)
        assertThat(checklist.tasks[1].completionDate).isEqualTo(1700000000L)
        assertThat(checklist.tasks[1].completedByUser?.firstName).isEqualTo("Alice")
        assertThat(checklist.othersCanAddTasks).isTrue
        assertThat(checklist.othersCanMarkTasksAsDone).isFalse
    }

    @Test
    fun `deserializes minimal Checklist`() {
        val json = """{"title":"Empty","tasks":[]}"""

        val checklist = gson.fromJson(json, Checklist::class.java)

        assertThat(checklist.title).isEqualTo("Empty")
        assertThat(checklist.tasks).isEmpty()
        assertThat(checklist.titleEntities).isNull()
        assertThat(checklist.othersCanAddTasks).isNull()
        assertThat(checklist.othersCanMarkTasksAsDone).isNull()
    }

    @Test
    fun `deserializes ChecklistTask with completed_by_chat`() {
        val json =
            """
            {"id":7,"text":"Done","completed_by_chat":{"id":-100,"type":"supergroup"}}
            """.trimIndent()

        val task = gson.fromJson(json, ChecklistTask::class.java)

        assertThat(task.id).isEqualTo(7)
        assertThat(task.text).isEqualTo("Done")
        assertThat(task.completedByChat?.id).isEqualTo(-100L)
    }
}
