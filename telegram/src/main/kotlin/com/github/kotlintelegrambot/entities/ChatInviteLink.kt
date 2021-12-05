package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * Represents a Telegram ChatInviteLink added in Bot API 5.1.
 */
data class ChatInviteLink(
    @SerializedName("invite_link") val inviteLink: String,
    @SerializedName("creator") val creator: User,
    @SerializedName("creates_join_request") val createsJoinRequest: Boolean,
    @SerializedName("is_primary") val isPrimary: Boolean,
    @SerializedName("is_revoked") val isRevoked: Boolean,
    @SerializedName("name") val name: String? = null,
    @SerializedName("expire_date") val expireDate: Long? = null,
    @SerializedName("member_limit") val memberLimit: Int? = null,
    @SerializedName("pending_join_request_count") val pendingJoinRequestCount: Int? = null,
)
