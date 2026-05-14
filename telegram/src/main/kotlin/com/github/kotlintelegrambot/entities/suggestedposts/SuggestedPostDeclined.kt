package com.github.kotlintelegrambot.entities.suggestedposts

import com.google.gson.annotations.SerializedName

/**
 * Describes a service message about the rejection of a suggested post. (Bot API 9.2)
 *
 * See https://core.telegram.org/bots/api#suggestedpostdeclined
 */
data class SuggestedPostDeclined(
    @SerializedName("suggested_post_message") val suggestedPostMessage: com.github.kotlintelegrambot.entities.Message? = null,
    @SerializedName("comment") val comment: String? = null,
)
