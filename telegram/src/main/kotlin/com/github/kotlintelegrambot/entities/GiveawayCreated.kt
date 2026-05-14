package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents a service message about the creation of a scheduled giveaway.
 *
 * See https://core.telegram.org/bots/api#giveawaycreated
 */
data class GiveawayCreated(
    @SerializedName("prize_star_count") val prizeStarCount: Int? = null,
)
