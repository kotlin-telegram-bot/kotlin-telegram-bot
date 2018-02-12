package me.ivmg.telegram.entities

import com.google.gson.annotations.SerializedName as Name

data class ChatMember(
    val user: User,
    val status: String,
    @Name("until_date") val forceReply: Int?,
    @Name("can_be_edited") val canBeEdited: Boolean?,
    @Name("can_change_info") val canChangeInfo: Boolean?,
    @Name("can_post_messages") val canPostMessages: Boolean?,
    @Name("can_edit_messages") val canEditMessages: Boolean?,
    @Name("can_delete_messages") val canDeleteMessages: Boolean?,
    @Name("can_invite_users") val canInviteUsers: Boolean?,
    @Name("can_restrict_members") val canRestrictMembers: Boolean?,
    @Name("can_pin_messages") val canPinMessages: Boolean?,
    @Name("can_promote_members") val canPromoteMembers: Boolean?,
    @Name("can_send_messages") val canSendMessages: Boolean?,
    @Name("can_send_media_messages") val canSendMediaMessages: Boolean?,
    @Name("can_send_other_messages") val canSendOtherMessages: Boolean?,
    @Name("can_add_web_page_previews") val canAddWebPagePreviews: Boolean?
)