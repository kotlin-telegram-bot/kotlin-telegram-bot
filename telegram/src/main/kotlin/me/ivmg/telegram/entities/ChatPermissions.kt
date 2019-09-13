package me.ivmg.telegram.entities.stickers

import com.google.gson.annotations.SerializedName as Name

data class ChatPermissions(
    @Name("can_send_messages") val canSendMessages: Boolean?,
    @Name("can_send_media_messages") val canSendMediaMessages: Boolean?,
    @Name("can_send_polls") val canSendPolls: Boolean?,
    @Name("canSendOtherMessages") val canSendOtherMessages: Boolean?,
    @Name("can_add_web_page_previews") val canAddWebPagePreviews: Boolean?,
    @Name("can_change_info") val canChangeInfo: Boolean?,
    @Name("can_invite_users") val canInviteUsers: Boolean?,
    @Name("can_pin_messages") val canPinMessages: Boolean?
)
