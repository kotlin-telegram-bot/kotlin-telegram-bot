package com.github.kotlintelegrambot.entities.gifts

import com.github.kotlintelegrambot.entities.MessageEntity
import com.github.kotlintelegrambot.entities.User
import com.google.gson.annotations.SerializedName

/**
 * Describes a gift received and owned by a user or a chat. (Bot API 9.0)
 *
 * See https://core.telegram.org/bots/api#ownedgift
 */
sealed class OwnedGift {
    /** Discriminator value sent on the wire as the `type` field. */
    abstract val type: String

    /** Describes a regular gift owned by a user or a chat. */
    data class Regular(
        @SerializedName("type") override val type: String = "regular",
        @SerializedName("gift") val gift: Gift,
        @SerializedName("owned_gift_id") val ownedGiftId: String? = null,
        @SerializedName("sender_user") val senderUser: User? = null,
        @SerializedName("send_date") val sendDate: Long,
        @SerializedName("text") val text: String? = null,
        @SerializedName("entities") val entities: List<MessageEntity>? = null,
        @SerializedName("is_private") val isPrivate: Boolean? = null,
        @SerializedName("is_saved") val isSaved: Boolean? = null,
        @SerializedName("can_be_upgraded") val canBeUpgraded: Boolean? = null,
        @SerializedName("is_upgrade_separate") val isUpgradeSeparate: Boolean? = null,
        @SerializedName("was_refunded") val wasRefunded: Boolean? = null,
        @SerializedName("convert_star_count") val convertStarCount: Int? = null,
        @SerializedName("prepaid_upgrade_star_count") val prepaidUpgradeStarCount: Int? = null,
        @SerializedName("unique_gift_number") val uniqueGiftNumber: Int? = null,
    ) : OwnedGift()

    /** Describes a unique gift received and owned by a user or a chat. */
    data class Unique(
        @SerializedName("type") override val type: String = "unique",
        @SerializedName("gift") val gift: UniqueGift,
        @SerializedName("owned_gift_id") val ownedGiftId: String? = null,
        @SerializedName("sender_user") val senderUser: User? = null,
        @SerializedName("send_date") val sendDate: Long,
        @SerializedName("is_saved") val isSaved: Boolean? = null,
        @SerializedName("can_be_transferred") val canBeTransferred: Boolean? = null,
        @SerializedName("transfer_star_count") val transferStarCount: Int? = null,
        @SerializedName("next_transfer_date") val nextTransferDate: Long? = null,
    ) : OwnedGift()
}
