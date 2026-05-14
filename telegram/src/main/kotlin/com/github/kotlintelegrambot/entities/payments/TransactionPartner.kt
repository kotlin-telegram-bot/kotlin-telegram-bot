package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.entities.gifts.Gift
import com.google.gson.annotations.SerializedName

/**
 * Describes the source of a transaction or its recipient for outgoing transactions. (Bot API 7.5)
 *
 * See https://core.telegram.org/bots/api#transactionpartner
 */
sealed class TransactionPartner {

    abstract val type: String

    /** Describes a transaction with a user. */
    data class User(
        @SerializedName("type") override val type: String = "user",
        @SerializedName("transaction_type") val transactionType: String,
        @SerializedName("user") val user: com.github.kotlintelegrambot.entities.User,
        @SerializedName("affiliate") val affiliate: AffiliateInfo? = null,
        @SerializedName("invoice_payload") val invoicePayload: String? = null,
        @SerializedName("subscription_period") val subscriptionPeriod: Int? = null,
        @SerializedName("paid_media") val paidMedia: List<PaidMedia>? = null,
        @SerializedName("paid_media_payload") val paidMediaPayload: String? = null,
        @SerializedName("gift") val gift: Gift? = null,
        @SerializedName("premium_subscription_duration") val premiumSubscriptionDuration: Int? = null,
    ) : TransactionPartner()

    /** Describes a withdrawal transaction with Fragment. */
    data class Fragment(
        @SerializedName("type") override val type: String = "fragment",
        @SerializedName("withdrawal_state") val withdrawalState: RevenueWithdrawalState? = null,
    ) : TransactionPartner()

    /** Describes a withdrawal transaction to the Telegram Ads platform. */
    data class TelegramAds(
        @SerializedName("type") override val type: String = "telegram_ads",
    ) : TransactionPartner()

    /** Describes a transaction with payment for paid broadcasting. (Bot API 7.11) */
    data class TelegramApi(
        @SerializedName("type") override val type: String = "telegram_api",
        @SerializedName("request_count") val requestCount: Int,
    ) : TransactionPartner()

    /** Describes the affiliate program that issued the transaction. (Bot API 8.1) */
    data class AffiliateProgram(
        @SerializedName("type") override val type: String = "affiliate_program",
        @SerializedName("sponsor_user") val sponsorUser: com.github.kotlintelegrambot.entities.User? = null,
        @SerializedName("commission_per_mille") val commissionPerMille: Int,
    ) : TransactionPartner()

    /** Describes a transaction with a chat. (Bot API 8.3) */
    data class Chat(
        @SerializedName("type") override val type: String = "chat",
        @SerializedName("chat") val chat: com.github.kotlintelegrambot.entities.Chat,
        @SerializedName("gift") val gift: Gift? = null,
    ) : TransactionPartner()

    /** Describes a transaction with an unknown source or recipient. */
    data class Other(
        @SerializedName("type") override val type: String = "other",
    ) : TransactionPartner()
}
