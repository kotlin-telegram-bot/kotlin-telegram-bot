package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents a message about a scheduled giveaway.
 *
 * See https://core.telegram.org/bots/api#giveaway
 */
data class Giveaway(
    @SerializedName("chats") val chats: List<Chat>,
    @SerializedName("winners_selection_date") val winnersSelectionDate: Long,
    @SerializedName("winner_count") val winnerCount: Int,
    @SerializedName("only_new_members") val onlyNewMembers: Boolean? = null,
    @SerializedName("has_public_winners") val hasPublicWinners: Boolean? = null,
    @SerializedName("prize_description") val prizeDescription: String? = null,
    @SerializedName("country_codes") val countryCodes: List<String>? = null,
    @SerializedName("prize_star_count") val prizeStarCount: Int? = null,
    @SerializedName("premium_subscription_month_count") val premiumSubscriptionMonthCount: Int? = null,
)
