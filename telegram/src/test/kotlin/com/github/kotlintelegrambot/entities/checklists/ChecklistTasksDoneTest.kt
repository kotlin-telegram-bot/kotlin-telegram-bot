package com.github.kotlintelegrambot.entities.checklists

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChecklistTasksDoneTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes ChecklistTasksDone with ids and message`() {
        val json =
            """
            {"marked_as_done_task_ids":[1,2,3],"marked_as_not_done_task_ids":[4]}
            """.trimIndent()

        val done = gson.fromJson(json, ChecklistTasksDone::class.java)

        assertThat(done.markedAsDoneTaskIds).containsExactly(1, 2, 3)
        assertThat(done.markedAsNotDoneTaskIds).containsExactly(4)
        assertThat(done.checklistMessage).isNull()
    }

    @Test
    fun `deserializes empty ChecklistTasksDone`() {
        val done = gson.fromJson("{}", ChecklistTasksDone::class.java)

        assertThat(done.markedAsDoneTaskIds).isNull()
        assertThat(done.markedAsNotDoneTaskIds).isNull()
        assertThat(done.checklistMessage).isNull()
    }
}
