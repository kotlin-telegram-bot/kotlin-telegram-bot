package com.github.kotlintelegrambot.entities.business

import com.github.kotlintelegrambot.entities.User
import com.google.gson.annotations.SerializedName as Name

/**
 * Describes the connection of the bot with a business account.
 *
 * https://core.telegram.org/bots/api#businessconnection (Bot API 7.2)
 *
 * @property canReply Deprecated since Bot API 9.0 — superseded by [rights]. `true` if the bot can
 *   send and edit messages in the private chats that had incoming messages in the last 24 hours.
 * @property rights Rights of the business bot (Bot API 9.0+).
 */
data class BusinessConnection(
    @Name("id") val id: String,
    @Name("user") val user: User,
    @Name("user_chat_id") val userChatId: Long,
    @Name("date") val date: Long,
    @Name("can_reply") val canReply: Boolean? = null,
    @Name("rights") val rights: BusinessBotRights? = null,
    @Name("is_enabled") val isEnabled: Boolean,
)
