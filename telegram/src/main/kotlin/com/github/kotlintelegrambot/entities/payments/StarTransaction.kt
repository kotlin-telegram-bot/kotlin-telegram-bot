package com.github.kotlintelegrambot.entities.payments

import com.google.gson.annotations.SerializedName

/**
 * Describes a Telegram Star transaction. (Bot API 7.5)
 *
 * See https://core.telegram.org/bots/api#startransaction
 */
data class StarTransaction(
    @SerializedName("id") val id: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("nanostar_amount") val nanostarAmount: Int? = null,
    @SerializedName("date") val date: Long,
    @SerializedName("source") val source: TransactionPartner? = null,
    @SerializedName("receiver") val receiver: TransactionPartner? = null,
)
