package com.github.kotlintelegrambot.entities.suggestedposts

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SuggestedPostPaidTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes SuggestedPostPaid with amount`() {
        val json = """{"currency":"XTR","amount":500}"""

        val paid = gson.fromJson(json, SuggestedPostPaid::class.java)

        assertThat(paid.currency).isEqualTo("XTR")
        assertThat(paid.amount).isEqualTo(500L)
        assertThat(paid.starAmount).isNull()
    }

    @Test
    fun `deserializes SuggestedPostPaid minimal`() {
        val json = """{"currency":"USD"}"""

        val paid = gson.fromJson(json, SuggestedPostPaid::class.java)

        assertThat(paid.currency).isEqualTo("USD")
        assertThat(paid.amount).isNull()
    }
}
