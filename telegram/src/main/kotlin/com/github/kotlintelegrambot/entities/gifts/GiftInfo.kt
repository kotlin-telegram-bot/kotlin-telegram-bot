package com.github.kotlintelegrambot.entities.gifts

import com.github.kotlintelegrambot.entities.MessageEntity
import com.google.gson.annotations.SerializedName

/**
 * Describes a service message about a regular gift that was sent or received. (Bot API 9.0)
 *
 * See https://core.telegram.org/bots/api#giftinfo
 */
data class GiftInfo(
    @SerializedName("gift") val gift: Gift,
    @SerializedName("owned_gift_id") val ownedGiftId: String? = null,
    @SerializedName("convert_star_count") val convertStarCount: Int? = null,
    @SerializedName("prepaid_upgrade_star_count") val prepaidUpgradeStarCount: Int? = null,
    @SerializedName("can_be_upgraded") val canBeUpgraded: Boolean? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("entities") val entities: List<MessageEntity>? = null,
    @SerializedName("is_private") val isPrivate: Boolean? = null,
)
