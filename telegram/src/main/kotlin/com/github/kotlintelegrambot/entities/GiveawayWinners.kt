package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents a message about the completion of a giveaway with public winners.
 *
 * See https://core.telegram.org/bots/api#giveawaywinners
 */
data class GiveawayWinners(
    @SerializedName("chat") val chat: Chat,
    @SerializedName("giveaway_message_id") val giveawayMessageId: Long,
    @SerializedName("winners_selection_date") val winnersSelectionDate: Long,
    @SerializedName("winner_count") val winnerCount: Int,
    @SerializedName("winners") val winners: List<User>,
    @SerializedName("additional_chat_count") val additionalChatCount: Int? = null,
    @SerializedName("prize_star_count") val prizeStarCount: Int? = null,
    @SerializedName("premium_subscription_month_count") val premiumSubscriptionMonthCount: Int? = null,
    @SerializedName("unclaimed_prize_count") val unclaimedPrizeCount: Int? = null,
    @SerializedName("only_new_members") val onlyNewMembers: Boolean? = null,
    @SerializedName("was_refunded") val wasRefunded: Boolean? = null,
    @SerializedName("prize_description") val prizeDescription: String? = null,
)
