package com.github.kotlintelegrambot.entities.payments

import com.github.kotlintelegrambot.entities.User
import com.google.gson.annotations.SerializedName as Name

/**
 * Sent when a paid media purchase succeeds.
 *
 * https://core.telegram.org/bots/api#paidmediapurchased (Bot API 7.10)
 */
data class PaidMediaPurchased(
    @Name("from") val from: User,
    @Name("paid_media_payload") val paidMediaPayload: String,
)
