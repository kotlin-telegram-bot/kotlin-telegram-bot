package com.github.kotlintelegrambot.entities.suggestedposts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SuggestedPostRefundedTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes SuggestedPostRefunded`() {
        val json = """{"reason":"post_deleted"}"""

        val refunded = gson.fromJson(json, SuggestedPostRefunded::class.java)

        assertThat(refunded.reason).isEqualTo("post_deleted")
        assertThat(refunded.suggestedPostMessage).isNull()
    }
}
