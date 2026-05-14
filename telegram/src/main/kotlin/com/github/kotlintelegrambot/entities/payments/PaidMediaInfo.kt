package com.github.kotlintelegrambot.entities.payments

import com.google.gson.annotations.SerializedName

/**
 * Describes the paid media added to a message. (Bot API 7.6)
 *
 * See https://core.telegram.org/bots/api#paidmediainfo
 */
data class PaidMediaInfo(
    @SerializedName("star_count") val starCount: Int,
    @SerializedName("paid_media") val paidMedia: List<PaidMedia>,
)
