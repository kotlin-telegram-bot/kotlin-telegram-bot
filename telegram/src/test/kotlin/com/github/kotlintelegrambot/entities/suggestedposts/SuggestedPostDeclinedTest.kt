package com.github.kotlintelegrambot.entities.suggestedposts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SuggestedPostDeclinedTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes SuggestedPostDeclined with comment`() {
        val json = """{"comment":"Not relevant"}"""

        val declined = gson.fromJson(json, SuggestedPostDeclined::class.java)

        assertThat(declined.comment).isEqualTo("Not relevant")
        assertThat(declined.suggestedPostMessage).isNull()
    }

    @Test
    fun `deserializes empty SuggestedPostDeclined`() {
        val declined = gson.fromJson("{}", SuggestedPostDeclined::class.java)

        assertThat(declined.comment).isNull()
    }
}
