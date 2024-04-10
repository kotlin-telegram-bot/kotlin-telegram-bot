package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName as Name

data class ChatJoinRequest(
    val chat: Chat,
    @Name("from")
    val from: User,
    @Name("user_chat_id")
    val userChatId: Long,
    @Name("date")
    val date: Long,
    @Name("invite_link")
    val inviteLink: InviteLink,
    @Name("pending_join_request_count")
    val pendingJoinRequestCount: Int,
    @Name("creates_join_request")
    val createsJoinRequest: Boolean,
    @Name("is_primary")
    val isPrimary: Boolean,
    @Name("is_revoked")
    val isRevoked: Boolean,
)
