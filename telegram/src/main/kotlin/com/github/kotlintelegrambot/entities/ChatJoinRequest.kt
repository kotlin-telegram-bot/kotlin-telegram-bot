package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

data class ChatJoinRequest(
    @SerializedName("chat") val chat: Chat,
    @SerializedName("from") val from: User,
    @SerializedName("date") val date: Long,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("invite_link") val inviteLink: ChatInviteLink? = null,
)
