package com.github.kotlintelegrambot.entities.payments

import com.google.gson.annotations.SerializedName

/**
 * Describes the state of a revenue withdrawal operation. (Bot API 7.5)
 *
 * See https://core.telegram.org/bots/api#revenuewithdrawalstate
 */
sealed class RevenueWithdrawalState {

    /** Discriminator value sent on the wire as the `type` field. */
    abstract val type: String

    /** The withdrawal is in progress. */
    data class Pending(
        @SerializedName("type") override val type: String = "pending",
    ) : RevenueWithdrawalState()

    /** The withdrawal succeeded. */
    data class Succeeded(
        @SerializedName("type") override val type: String = "succeeded",
        @SerializedName("date") val date: Long,
        @SerializedName("url") val url: String,
    ) : RevenueWithdrawalState()

    /** The withdrawal failed and the transaction was refunded. */
    data class Failed(
        @SerializedName("type") override val type: String = "failed",
    ) : RevenueWithdrawalState()
}
