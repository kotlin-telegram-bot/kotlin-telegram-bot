package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GiveawayCompletedTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes GiveawayCompleted minimal`() {
        val json = """{"winner_count":3}"""

        val completed = gson.fromJson(json, GiveawayCompleted::class.java)

        assertThat(completed.winnerCount).isEqualTo(3)
        assertThat(completed.unclaimedPrizeCount).isNull()
        assertThat(completed.giveawayMessage).isNull()
    }

    @Test
    fun `deserializes GiveawayCompleted with unclaimed_prize_count`() {
        val json = """{"winner_count":3,"unclaimed_prize_count":1}"""

        val completed = gson.fromJson(json, GiveawayCompleted::class.java)

        assertThat(completed.winnerCount).isEqualTo(3)
        assertThat(completed.unclaimedPrizeCount).isEqualTo(1)
    }
}
