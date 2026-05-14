package com.github.kotlintelegrambot.entities.suggestedposts

import com.google.gson.annotations.SerializedName

/**
 * Describes a service message about the failed approval of a suggested post. (Bot API 9.2)
 *
 * See https://core.telegram.org/bots/api#suggestedpostapprovalfailed
 */
data class SuggestedPostApprovalFailed(
    @SerializedName("suggested_post_message") val suggestedPostMessage: com.github.kotlintelegrambot.entities.Message? = null,
    @SerializedName("price") val price: SuggestedPostPrice? = null,
)
