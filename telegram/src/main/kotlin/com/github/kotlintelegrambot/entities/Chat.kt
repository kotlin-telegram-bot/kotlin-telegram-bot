package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.files.ChatPhoto
import com.google.gson.annotations.SerializedName

/**
 * Represents a chat.
 * https://core.telegram.org/bots/api#chat
 */
data class Chat(
    @SerializedName("id") val id: Long,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("photo") val photo: ChatPhoto? = null,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("invite_link") val inviteLink: String? = null,
    @SerializedName("pinned_message") val pinnedMessage: String? = null,
    @SerializedName("permissions") val permissions: ChatPermissions? = null,
    @SerializedName("slow_mode_delay") val slowModeDelay: Int? = null,
    @SerializedName("sticker_set_name") val stickerSetName: String? = null,
    @SerializedName("can_set_sticker_set") val canSetStickerSet: Boolean? = null,
    @SerializedName("linked_chat_id") val linkedChatId: Long? = null,
    @SerializedName("location") val location: ChatLocation? = null,
)
