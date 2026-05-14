package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GiveawayCreatedTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes GiveawayCreated with prize_star_count`() {
        val json = """{"prize_star_count":50}"""

        val created = gson.fromJson(json, GiveawayCreated::class.java)

        assertThat(created.prizeStarCount).isEqualTo(50)
    }

    @Test
    fun `deserializes empty GiveawayCreated`() {
        val json = "{}"

        val created = gson.fromJson(json, GiveawayCreated::class.java)

        assertThat(created.prizeStarCount).isNull()
    }
}
