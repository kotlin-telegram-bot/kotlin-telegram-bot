package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents a chat.
 *
 * As of Bot API 7.3 this is the **minimal** chat representation — only the fields that arrive in
 * nested positions (`Message.chat`, `Update.chat_member.chat`, etc.). Use [com.github.kotlintelegrambot.entities.ChatFullInfo]
 * (returned by `getChat`) for the rich profile fields (bio, description, photo, business info,
 * accent colors, available reactions, etc.).
 *
 * https://core.telegram.org/bots/api#chat
 */
data class Chat(
    @SerializedName("id") val id: Long,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("is_forum") val isForum: Boolean? = null,
    @SerializedName("is_direct_messages") val isDirectMessages: Boolean? = null,
)
