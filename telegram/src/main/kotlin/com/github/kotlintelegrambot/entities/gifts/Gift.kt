package com.github.kotlintelegrambot.entities.gifts

import com.github.kotlintelegrambot.entities.stickers.Sticker
import com.google.gson.annotations.SerializedName

/**
 * Represents a gift that can be sent by the bot. (Bot API 8.0)
 *
 * See https://core.telegram.org/bots/api#gift
 */
data class Gift(
    @SerializedName("id") val id: String,
    @SerializedName("sticker") val sticker: Sticker,
    @SerializedName("star_count") val starCount: Int,
    @SerializedName("upgrade_star_count") val upgradeStarCount: Int? = null,
    @SerializedName("total_count") val totalCount: Int? = null,
    @SerializedName("remaining_count") val remainingCount: Int? = null,
    @SerializedName("is_premium") val isPremium: Boolean? = null,
    @SerializedName("has_colors") val hasColors: Boolean? = null,
    @SerializedName("personal_total_count") val personalTotalCount: Int? = null,
    @SerializedName("personal_remaining_count") val personalRemainingCount: Int? = null,
    @SerializedName("background") val background: GiftBackground? = null,
    @SerializedName("unique_gift_variant_count") val uniqueGiftVariantCount: Int? = null,
    @SerializedName("publisher_chat") val publisherChat: com.github.kotlintelegrambot.entities.Chat? = null,
)
