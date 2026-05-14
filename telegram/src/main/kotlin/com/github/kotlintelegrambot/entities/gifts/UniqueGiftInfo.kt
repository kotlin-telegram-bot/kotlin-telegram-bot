package com.github.kotlintelegrambot.entities.gifts

import com.google.gson.annotations.SerializedName

/**
 * Describes a service message about a unique gift that was sent or received. (Bot API 9.0)
 *
 * See https://core.telegram.org/bots/api#uniquegiftinfo
 */
data class UniqueGiftInfo(
    @SerializedName("gift") val gift: UniqueGift,
    /** Origin of the gift. Currently, either "upgrade" for upgraded regular gifts, "transfer" or "resale" for unique gifts. */
    @SerializedName("origin") val origin: String,
    @SerializedName("owned_gift_id") val ownedGiftId: String? = null,
    @SerializedName("transfer_star_count") val transferStarCount: Int? = null,
    @SerializedName("next_transfer_date") val nextTransferDate: Long? = null,
)
