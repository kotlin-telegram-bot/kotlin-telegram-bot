package com.github.kotlintelegrambot.entities

import com.google.gson.annotations.SerializedName

/**
 * This object represents changes in the status of a chat member.
 * https://core.telegram.org/bots/api#chatmemberupdated
 */

class ChatMemberUpdated(
    val chat: Chat,
    val from: User,
    val date: Int,
    @SerializedName("old_chat_member") val oldChatMember: ChatMember,
    @SerializedName("new_chat_member") val newChatMember: ChatMember,
    @SerializedName("invite_link") val inviteLink: ChatInviteLink? = null
)
