package com.github.kotlintelegrambot.entities.payments

import com.google.gson.annotations.SerializedName

/**
 * Contains information about the affiliate that received a commission via the affiliate program.
 * (Bot API 8.1)
 *
 * See https://core.telegram.org/bots/api#affiliateinfo
 */
data class AffiliateInfo(
    @SerializedName("affiliate_user") val affiliateUser: com.github.kotlintelegrambot.entities.User? = null,
    @SerializedName("affiliate_chat") val affiliateChat: com.github.kotlintelegrambot.entities.Chat? = null,
    @SerializedName("commission_per_mille") val commissionPerMille: Int,
    @SerializedName("amount") val amount: Int,
    @SerializedName("nanostar_amount") val nanostarAmount: Int? = null,
)
