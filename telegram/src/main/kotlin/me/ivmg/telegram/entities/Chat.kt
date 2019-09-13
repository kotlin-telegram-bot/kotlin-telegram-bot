package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name
import me.ivmg.telegram.entities.stickers.ChatPermissions

data class Chat(
    val id: Long,
    val type: String,
    val title: String? = null,
    val username: String? = null,
    @Name("first_name") val firstName: String? = null,
    @Name("last_name") val lastName: String? = null,
    val photo: ChatPhoto? = null,
    val description: String? = null,
    @Name("invite_link") val inviteLink: String? = null,
    @Name("pinned_message") val pinnedMessage: String? = null,
    val permissions: ChatPermissions? = null,
    @Name("sticker_set_name") val stickerSetName: String? = null,
    @Name("can_set_sticker_set") val canSetStickerSet: Boolean? = null
)
