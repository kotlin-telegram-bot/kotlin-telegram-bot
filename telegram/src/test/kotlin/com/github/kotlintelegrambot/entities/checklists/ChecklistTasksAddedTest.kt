package com.github.kotlintelegrambot.entities.checklists

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChecklistTasksAddedTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes ChecklistTasksAdded with tasks`() {
        val json =
            """
            {"tasks":[{"id":1,"text":"A"},{"id":2,"text":"B"}]}
            """.trimIndent()

        val added = gson.fromJson(json, ChecklistTasksAdded::class.java)

        assertThat(added.tasks).hasSize(2)
        assertThat(added.tasks[0].text).isEqualTo("A")
        assertThat(added.checklistMessage).isNull()
    }
}
