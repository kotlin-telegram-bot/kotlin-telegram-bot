package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.network.serialization.GsonFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GiveawayWinnersTest {

    private val gson = GsonFactory.createForApiClient()

    @Test
    fun `deserializes GiveawayWinners from JSON`() {
        val json = """
            {
              "chat":{"id":-1,"type":"channel"},
              "giveaway_message_id":99,
              "winners_selection_date":1700000000,
              "winner_count":2,
              "winners":[
                {"id":1,"is_bot":false,"first_name":"Alice"},
                {"id":2,"is_bot":false,"first_name":"Bob"}
              ],
              "additional_chat_count":3,
              "prize_star_count":100,
              "premium_subscription_month_count":6,
              "unclaimed_prize_count":0,
              "only_new_members":true,
              "was_refunded":false,
              "prize_description":"prize"
            }
        """.trimIndent()

        val winners = gson.fromJson(json, GiveawayWinners::class.java)

        assertThat(winners.chat.id).isEqualTo(-1L)
        assertThat(winners.giveawayMessageId).isEqualTo(99L)
        assertThat(winners.winnersSelectionDate).isEqualTo(1700000000L)
        assertThat(winners.winnerCount).isEqualTo(2)
        assertThat(winners.winners).hasSize(2)
        assertThat(winners.winners[1].firstName).isEqualTo("Bob")
        assertThat(winners.additionalChatCount).isEqualTo(3)
        assertThat(winners.prizeStarCount).isEqualTo(100)
        assertThat(winners.premiumSubscriptionMonthCount).isEqualTo(6)
        assertThat(winners.unclaimedPrizeCount).isEqualTo(0)
        assertThat(winners.onlyNewMembers).isTrue()
        assertThat(winners.wasRefunded).isFalse()
        assertThat(winners.prizeDescription).isEqualTo("prize")
    }
}
