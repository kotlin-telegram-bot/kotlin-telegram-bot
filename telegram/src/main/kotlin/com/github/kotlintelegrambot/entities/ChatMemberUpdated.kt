package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

data class ChatMemberUpdated(
    @SerializedName("chat") val chat: Chat,
    @SerializedName("from") val from: User,
    @SerializedName("date") val date: Long,
    @SerializedName("old_chat_member") val oldChatMember: ChatMember,
    @SerializedName("new_chat_member") val newChatMember: ChatMember,
    @SerializedName("invite_link") val inviteLink: ChatInviteLink? = null,
    @SerializedName("via_chat_folder_invite_link") val viaChatFolderInviteLink: Boolean? = null,
)
