package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents a service message about the completion of a giveaway without public winners.
 *
 * See https://core.telegram.org/bots/api#giveawaycompleted
 */
data class GiveawayCompleted(
    @SerializedName("winner_count") val winnerCount: Int,
    @SerializedName("unclaimed_prize_count") val unclaimedPrizeCount: Int? = null,
    @SerializedName("giveaway_message") val giveawayMessage: Message? = null,
)
