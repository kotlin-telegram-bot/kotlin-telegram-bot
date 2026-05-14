package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.files.PhotoSize
import com.google.gson.annotations.SerializedName as Name

/**
 * Contains information about a chat that was shared with the bot using a
 * [KeyboardButtonRequestChat] button.
 *
 * Bot API 6.5, expanded with `title`, `username` and `photo` in 7.2.
 *
 * See https://core.telegram.org/bots/api#chatshared
 */
data class ChatShared(
    @Name("request_id") val requestId: Int,
    @Name("chat_id") val chatId: Long,
    @Name("title") val title: String? = null,
    @Name("username") val username: String? = null,
    @Name("photo") val photo: List<PhotoSize>? = null,
)
