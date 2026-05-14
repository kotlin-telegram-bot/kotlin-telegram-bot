package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Describes the source of a chat boost.
 *
 * See https://core.telegram.org/bots/api#chatboostsource
 */
sealed class ChatBoostSource {
    /** Discriminator value sent on the wire as the `source` field. */
    abstract val source: String

    /** The boost was obtained by subscribing to Telegram Premium or by gifting a Premium subscription. */
    data class Premium(
        @SerializedName("source") override val source: String = "premium",
        @SerializedName("user") val user: User,
    ) : ChatBoostSource()

    /** The boost was obtained by the creation of Telegram Premium gift codes. */
    data class GiftCode(
        @SerializedName("source") override val source: String = "gift_code",
        @SerializedName("user") val user: User,
    ) : ChatBoostSource()

    /** The boost was obtained by the creation of a Telegram Premium or a Telegram Star giveaway. */
    data class Giveaway(
        @SerializedName("source") override val source: String = "giveaway",
        @SerializedName("giveaway_message_id") val giveawayMessageId: Long,
        @SerializedName("user") val user: User? = null,
        @SerializedName("prize_star_count") val prizeStarCount: Int? = null,
        @SerializedName("is_unclaimed") val isUnclaimed: Boolean? = null,
    ) : ChatBoostSource()
}
