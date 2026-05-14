package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Contains information about a chat boost.
 *
 * See https://core.telegram.org/bots/api#chatboost
 */
data class ChatBoost(
    @SerializedName("boost_id") val boostId: String,
    @SerializedName("add_date") val addDate: Long,
    @SerializedName("expiration_date") val expirationDate: Long,
    @SerializedName("source") val source: ChatBoostSource,
)
