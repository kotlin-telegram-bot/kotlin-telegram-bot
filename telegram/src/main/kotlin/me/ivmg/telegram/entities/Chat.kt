package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class Chat(
    val id: Long,
    val type: String,
    val title: String?,
    val username: String?,
    @Name("first_name") val firstName: String?,
    @Name("last_name") val lastName: String?,
    @Name("all_members_are_administrators") val allMembersAreAdministrators: Boolean?,
    val photo: ChatPhoto?,
    val description: String?,
    @Name("invite_link") val inviteLink: String?,
    @Name("pinned_message") val pinnedMessage: String?,
    @Name("sticker_set_name") val stickerSetName: String?,
    @Name("can_set_sticker_set") val canSetStickerSet: Boolean?
)
