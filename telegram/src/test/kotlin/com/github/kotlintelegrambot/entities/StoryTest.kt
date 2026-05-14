package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StoryTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes Story from JSON`() {
        val json = """{"chat":{"id":-100,"type":"channel"},"id":17}"""

        val story = gson.fromJson(json, Story::class.java)

        assertThat(story.chat.id).isEqualTo(-100L)
        assertThat(story.id).isEqualTo(17)
    }
}
