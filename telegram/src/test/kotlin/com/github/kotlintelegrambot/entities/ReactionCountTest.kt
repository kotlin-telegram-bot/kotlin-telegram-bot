package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ReactionCountTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes ReactionCount with total_count`() {
        // Note: only total_count is asserted because polymorphic deserialization of ReactionType
        // (a sealed class with no JsonDeserializer registered) cannot be exercised here.
        val json = """{"total_count":7}"""

        val rc = gson.fromJson(json, ReactionCount::class.java)

        assertThat(rc.totalCount).isEqualTo(7)
    }
}
