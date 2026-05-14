package com.github.kotlintelegrambot.entities.payments

import com.google.gson.annotations.SerializedName

/**
 * Describes an amount of Telegram Stars. (Bot API 9.0)
 *
 * See https://core.telegram.org/bots/api#staramount
 */
data class StarAmount(
    /** Integer amount of Telegram Stars, rounded to 0; can be negative. */
    @SerializedName("amount") val amount: Int,
    /**
     * The number of 1/1000000000 shares of Telegram Stars; from -999999999 to 999999999;
     * can be negative if and only if amount is non-positive.
     */
    @SerializedName("nanostar_amount") val nanostarAmount: Int? = null,
)
