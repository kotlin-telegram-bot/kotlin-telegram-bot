package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GiveawayTest {
    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes Giveaway from JSON`() {
        val json =
            """
            {
              "chats":[{"id":-1,"type":"channel"}],
              "winners_selection_date":1700000000,
              "winner_count":5,
              "only_new_members":true,
              "has_public_winners":false,
              "prize_description":"a year of premium",
              "country_codes":["US","ES"],
              "prize_star_count":100,
              "premium_subscription_month_count":12
            }
            """.trimIndent()

        val giveaway = gson.fromJson(json, Giveaway::class.java)

        assertThat(giveaway.chats).hasSize(1)
        assertThat(giveaway.chats[0].id).isEqualTo(-1L)
        assertThat(giveaway.winnersSelectionDate).isEqualTo(1700000000L)
        assertThat(giveaway.winnerCount).isEqualTo(5)
        assertThat(giveaway.onlyNewMembers).isTrue()
        assertThat(giveaway.hasPublicWinners).isFalse()
        assertThat(giveaway.prizeDescription).isEqualTo("a year of premium")
        assertThat(giveaway.countryCodes).containsExactly("US", "ES")
        assertThat(giveaway.prizeStarCount).isEqualTo(100)
        assertThat(giveaway.premiumSubscriptionMonthCount).isEqualTo(12)
    }
}
