package com.github.kotlintelegrambot.entities.suggestedposts

import com.google.gson.annotations.SerializedName

/**
 * Contains information about a suggested post. (Bot API 9.2)
 *
 * See https://core.telegram.org/bots/api#suggestedpostinfo
 */
data class SuggestedPostInfo(
    @SerializedName("state") val state: String,
    @SerializedName("price") val price: SuggestedPostPrice? = null,
    @SerializedName("send_date") val sendDate: Long? = null,
)
