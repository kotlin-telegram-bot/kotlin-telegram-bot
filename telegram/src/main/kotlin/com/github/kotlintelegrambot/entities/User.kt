package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents a Telegram user or bot.
 */
data class User(
    @SerializedName("id") val id: Long,
    @SerializedName("is_bot") val isBot: Boolean,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("language_code") val languageCode: String? = null,
    @SerializedName("can_join_groups") val canJoinGroups: Boolean? = null,
    @SerializedName("can_read_all_group_messages") val canReadAllGroupMessages: Boolean? = null,
    @SerializedName("supports_inline_queries") val supportsInlineQueries: Boolean? = null
)
