package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PaidMessagePriceChangedTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes PaidMessagePriceChanged`() {
        val json = """{"paid_message_star_count":15}"""

        val event = gson.fromJson(json, PaidMessagePriceChanged::class.java)

        assertThat(event.paidMessageStarCount).isEqualTo(15)
    }
}
