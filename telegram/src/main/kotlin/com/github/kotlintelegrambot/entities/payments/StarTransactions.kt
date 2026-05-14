package com.github.kotlintelegrambot.entities.payments

import com.google.gson.annotations.SerializedName

/**
 * Contains a list of Telegram Star transactions. (Bot API 7.5)
 *
 * See https://core.telegram.org/bots/api#startransactions
 */
data class StarTransactions(
    @SerializedName("transactions") val transactions: List<StarTransaction>,
)
