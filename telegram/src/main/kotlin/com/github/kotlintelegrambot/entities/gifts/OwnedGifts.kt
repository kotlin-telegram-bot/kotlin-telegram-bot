package com.github.kotlintelegrambot.entities.gifts

import com.google.gson.annotations.SerializedName

/**
 * Contains the list of gifts received and owned by a user or a chat. (Bot API 9.0)
 *
 * See https://core.telegram.org/bots/api#ownedgifts
 */
data class OwnedGifts(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("gifts") val gifts: List<OwnedGift>,
    @SerializedName("next_offset") val nextOffset: String? = null,
)
